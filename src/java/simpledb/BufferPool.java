package simpledb;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool checks that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;
    private static final int ABORT_UPPER_TIME = 300;
    private static final int SLEEP_TIME = 200;

    private int pageNum = 0;
    private Map<PageId, Node> bufferedPages = null;
    private Node head = null;
    private Node end = null;

    //proj4
    private LockManager lockManager;
    private Map<TransactionId, Long> currentTransactions;

    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // some code goes here
        this.pageNum = numPages;
        this.bufferedPages = new HashMap<PageId, Node>();
        currentTransactions = new ConcurrentHashMap<TransactionId, Long>();
        lockManager = new LockManager();
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public  Page getPage(TransactionId tid, PageId pid, Permissions perm)
        throws TransactionAbortedException, DbException {
        // some code goes here
        //return null;

        //for new transactions, record time.
        if (!currentTransactions.containsKey(tid)) {
            long time = System.currentTimeMillis();
            currentTransactions.put(tid, time);
        }

        boolean isDenied = lockManager.grantLock(pid, tid, perm);
        //put on sleep if denied
        while(isDenied){
            if ((System.currentTimeMillis() - currentTransactions.get(tid))
                 > ABORT_UPPER_TIME) {
                throw new TransactionAbortedException();
            }

            try {
                Thread.sleep(SLEEP_TIME);
                //attempt to get lock again.
                isDenied = lockManager.grantLock(pid, tid, perm);
            } catch (InterruptedException e){
                e.printStackTrace();
                System.exit(0);
            }
        }

        Page retrievedPage = null;
        if (!bufferedPages.containsKey(pid)) {
            if (bufferedPages.size() >= pageNum) {
                evictPage();
            }

            retrievedPage = Database.getCatalog().getDbFile(pid.getTableId())
                            .readPage(pid);

            updateLruWithNewNode(pid, retrievedPage);
        } else {
            retrievedPage = bufferedPages.get(pid).page;
            Node node = bufferedPages.get(pid);
            removeNode(node);
            changeHead(node);
        }

        return retrievedPage;
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for proj4
        lockManager.releaseLock(pid, tid);
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public void transactionComplete(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for proj1\
    	//should always commit, simply call transactionComplete(tid,true)
    	transactionComplete(tid, true);
    	
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for proj1
        //return false;
        return lockManager.hasLocks(tid, p);
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(TransactionId tid, boolean commit)
        throws IOException {
        // some code goes here
        // not necessary for proj1

    	
    	//commit, flush the pages
    	if(commit){
    		for(PageId pageId : bufferedPages.keySet()){
    			Page page = bufferedPages.get(pageId).page;
    			//dirty page
    			if(page.isDirty() != null && tid.equals(page.isDirty())){
    				flushPage(pageId);
    				page.setBeforeImage();
    			}
    			
    		}
    	}
    	else{
    		for(PageId pageId : bufferedPages.keySet()){
    			Page page = bufferedPages.get(pageId).page;
    			//dirty page, revert changes
    			if(page.isDirty() != null && tid.equals(page.isDirty())){
    				Node n = bufferedPages.get(pageId);
    				n.page = page.getBeforeImage();
    				bufferedPages.put(pageId, n);
    			}
    			
    		}
    	}
    	
    	//release the lock
    	lockManager.releaseAllTransactionLocks(tid);
    	currentTransactions.remove(tid);
    	
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to(Lock 
     * acquisition is not needed for lab2). May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and updates cached versions of any pages that have 
     * been dirtied so that future requests see up-to-date pages. 
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public void insertTuple(TransactionId tid, int tableId, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for proj1
        try {
            List<Page> pages = Database.getCatalog().getDbFile(tableId)
                        .insertTuple(tid, t);
            for (Page page : pages) {
                page.markDirty(true, tid);
                updateLruWithNewNode(page.getId(), page);
            }
        } catch (TransactionAbortedException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (DbException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit.  Does not need to update cached versions of any pages that have 
     * been dirtied, as it is not possible that a new page was created during the deletion
     * (note difference from addTuple).
     *
     * @param tid the transaction adding the tuple.
     * @param t the tuple to add
     */
    public  void deleteTuple(TransactionId tid, Tuple t)
        throws DbException, TransactionAbortedException {
        // some code goes here
        // not necessary for proj1
        try {
            Page page = Database.getCatalog().getDbFile(t.getRecordId()
                .getPageId().getTableId()).deleteTuple(tid, t);

            page.markDirty(true, tid);
            updateLruWithNewNode(page.getId(), page);
        } catch (DbException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (TransactionAbortedException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        // some code goes here
        // not necessary for proj1
        for (PageId key : bufferedPages.keySet()) {
            flushPage(key);
        }
    }

    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.
    */
    public synchronized void discardPage(PageId pid) {
        // some code goes here
    // not necessary for proj1
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private synchronized  void flushPage(PageId pid) throws IOException {
        // some code goes here
        // not necessary for proj1
        try {
            Page page = bufferedPages.get(pid).page;
            page.markDirty(false, null);
            DbFile dbFile = Database.getCatalog()
                                .getDbFile(((HeapPageId)pid).getTableId());
            dbFile.writePage(page);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws DbException {
        // some code goes here
        // not necessary for proj1
        //flush the page first, which is the end node
        Node evictedNode = end;

        Page retrievedPage = Database.getCatalog()
                            .getDbFile(evictedNode.pageId.getTableId())
                            .readPage(evictedNode.pageId);

        //find page that is not dirty
        while (evictedNode != null && retrievedPage.isDirty() != null) {
            evictedNode = evictedNode.pre;
            retrievedPage = Database.getCatalog()
                            .getDbFile(evictedNode.pageId.getTableId())
                            .readPage(evictedNode.pageId);
        }

        if (evictedNode == null) {
            throw new DbException("All pages are dirty.");
        }

        try {
            flushPage(evictedNode.pageId);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        //remove the page
        bufferedPages.remove(evictedNode.pageId);
        removeNode(evictedNode);
    }

    //A methods to change the head node of double-list.
    private void changeHead(Node node){
        node.next = head;
        node.pre = head == null ? null : head.pre;
 
        head = node;
 
        if(end == null)
            end = head;
    }

    //A method to remove the node of double-list.
    private void removeNode(Node node){
        if (node.pre == null) {
            this.head = node.next;
        } else {
            node.pre.next = node.next;
        }

        if (node.next == null) {
            end = node.pre;
        } else {
            node.next.pre = node.pre;
        } 
    }

    //A method to update LRU cache when page are accessed.
    private void updateLruWithNewNode(PageId pid, Page retrievedPage) {
        Node node = new Node(pid, retrievedPage);
        changeHead(node);
        bufferedPages.put(pid, node);
    }
}

//Add a new class to implement LRU cache
class Node {
    PageId pageId;
    Page page;
    Node pre;
    Node next;

    public Node(PageId id, Page p) {
        this.pageId = id;
        this.page = p;
    }
}

