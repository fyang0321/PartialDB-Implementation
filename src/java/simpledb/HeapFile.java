// package simpledb;

// import java.io.*;
// import java.util.*;

// /**
//  * HeapFile is an implementation of a DbFile that stores a collection of tuples
//  * in no particular order. Tuples are stored on pages, each of which is a fixed
//  * size, and the file is simply a collection of those pages. HeapFile works
//  * closely with HeapPage. The format of HeapPages is described in the HeapPage
//  * constructor.
//  * 
//  * @see simpledb.HeapPage#HeapPage
//  * @author Sam Madden
//  */
// public class HeapFile implements DbFile {
//     private File file = null;
//     private TupleDesc tupleDesc = null;
//     private int heapFileId;
//     /**
//      * Constructs a heap file backed by the specified file.
//      * 
//      * @param f
//      *            the file that stores the on-disk backing store for this heap
//      *            file.
//      */
//     public HeapFile(File f, TupleDesc td) {
//         // some code goes here
//         this.file = f;
//         this.tupleDesc = td;
//         this.heapFileId = this.file.getAbsoluteFile().hashCode();
//     }

//     /**
//      * Returns the File backing this HeapFile on disk.
//      * 
//      * @return the File backing this HeapFile on disk.
//      */
//     public File getFile() {
//         // some code goes here
//         return this.file;
//     }

//     /**
//      * Returns an ID uniquely identifying this HeapFile. Implementation note:
//      * you will need to generate this tableid somewhere ensure that each
//      * HeapFile has a "unique id," and that you always return the same value for
//      * a particular HeapFile. We suggest hashing the absolute file name of the
//      * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
//      * 
//      * @return an ID uniquely identifying this HeapFile.
//      */
//     public int getId() {
//         // some code goes here
//         //throw new UnsupportedOperationException("implement this");
//         return this.heapFileId;
//     }

//     /**
//      * Returns the TupleDesc of the table stored in this DbFile.
//      * 
//      * @return TupleDesc of this DbFile.
//      */
//     public TupleDesc getTupleDesc() {
//         // some code goes here
//         //throw new UnsupportedOperationException("implement this");
//         return this.tupleDesc;
//     }

//     // see DbFile.java for javadocs
//     public Page readPage(PageId pid) {
//         // some code goes here
//         //return null;
//         Page newPage = null;
//         try {
//             RandomAccessFile randomReader = new RandomAccessFile(this.file, "r");
//             //adjust pointer to right offset;
//             randomReader.seek(pid.pageNumber() * BufferPool.PAGE_SIZE);
//             byte[] data = new byte[BufferPool.PAGE_SIZE];
//             randomReader.read(data, 0, BufferPool.PAGE_SIZE);
//             randomReader.close();

//             newPage = new HeapPage((HeapPageId)pid, data);
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//             System.exit(0);
//         } catch (IOException e) {
//             e.printStackTrace();
//             System.exit(0);
//         }

//         return newPage;
//     }

//     // see DbFile.java for javadocs
//     public void writePage(Page page) throws IOException {
//         // some code goes here
//         // not necessary for proj1
//     }

//     /**
//      * Returns the number of pages in this HeapFile.
//      */
//     public int numPages() {
//         // some code goes here
//         //return 0;
//         //get the size of the file
//         long len = this.file.length();
//         int num = (int) (len / BufferPool.PAGE_SIZE);

//         return len % BufferPool.PAGE_SIZE <= 0 ? num : num + 1;
//     }

//     // see DbFile.java for javadocs
//     public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
//             throws DbException, IOException, TransactionAbortedException {
//         // some code goes here
//         return null;
//         // not necessary for proj1
//     }

//     // see DbFile.java for javadocs
//     public Page deleteTuple(TransactionId tid, Tuple t) throws DbException,
//             TransactionAbortedException {
//         // some code goes here
//         return null;
//         // not necessary for proj1
//     }

//     // see DbFile.java for javadocs
//     public DbFileIterator iterator(TransactionId tid) {
//         // some code goes here
//         //return null;
//         int totalPage = numPages();
//         List<Tuple> tuples = new ArrayList<Tuple>();

//         for (int i = 0; i < totalPage; i++) {
//             HeapPage page = null;
//             PageId pid = new HeapPageId(this.heapFileId, i);
//             try {
//                 page = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
//             } catch(TransactionAbortedException e) {
//                 e.printStackTrace();
//                 System.exit(0);
//             } catch(DbException e) {
//                 e.printStackTrace();
//                 System.exit(0);
//             }
//             tuples.addAll(page.getAllTuples());
//         }

//         return new HeapFileIterator(tuples);
//     }

// }


// package simpledb;

// import java.io.*;
// import java.util.*;

// /**
//  * HeapFile is an implementation of a DbFile that stores a collection of tuples
//  * in no particular order. Tuples are stored on pages, each of which is a fixed
//  * size, and the file is simply a collection of those pages. HeapFile works
//  * closely with HeapPage. The format of HeapPages is described in the HeapPage
//  * constructor.
//  * 
//  * @see simpledb.HeapPage#HeapPage
//  * @author Sam Madden
//  */
// public class HeapFile implements DbFile {
//     private File file = null;
//     private TupleDesc tupleDesc = null;
//     private int heapFileId;
//     /**
//      * Constructs a heap file backed by the specified file.
//      * 
//      * @param f
//      *            the file that stores the on-disk backing store for this heap
//      *            file.
//      */
//     public HeapFile(File f, TupleDesc td) {
//         // some code goes here
//         this.file = f;
//         this.tupleDesc = td;
//         this.heapFileId = this.file.getAbsoluteFile().hashCode();
//     }

//     /**
//      * Returns the File backing this HeapFile on disk.
//      * 
//      * @return the File backing this HeapFile on disk.
//      */
//     public File getFile() {
//         // some code goes here
//         return this.file;
//     }

//     /**
//      * Returns an ID uniquely identifying this HeapFile. Implementation note:
//      * you will need to generate this tableid somewhere ensure that each
//      * HeapFile has a "unique id," and that you always return the same value for
//      * a particular HeapFile. We suggest hashing the absolute file name of the
//      * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
//      * 
//      * @return an ID uniquely identifying this HeapFile.
//      */
//     public int getId() {
//         // some code goes here
//         //throw new UnsupportedOperationException("implement this");
//         return this.heapFileId;
//     }

//     /**
//      * Returns the TupleDesc of the table stored in this DbFile.
//      * 
//      * @return TupleDesc of this DbFile.
//      */
//     public TupleDesc getTupleDesc() {
//         // some code goes here
//         //throw new UnsupportedOperationException("implement this");
//         return this.tupleDesc;
//     }

//     // see DbFile.java for javadocs
//     public Page readPage(PageId pid) {
//         // some code goes here
//         //return null;
//         Page newPage = null;
//         try {
//             RandomAccessFile randomReader = new RandomAccessFile(this.file, "r");
//             //adjust pointer to right offset;
//             randomReader.seek(pid.pageNumber() * BufferPool.PAGE_SIZE);
//             byte[] data = new byte[BufferPool.PAGE_SIZE];
//             randomReader.read(data, 0, BufferPool.PAGE_SIZE);
//             randomReader.close();

//             newPage = new HeapPage((HeapPageId)pid, data);
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//             System.exit(0);
//         } catch (IOException e) {
//             e.printStackTrace();
//             System.exit(0);
//         }

//         return newPage;
//     }

//     // see DbFile.java for javadocs
//     public void writePage(Page page) throws IOException {
//         // some code goes here
//         // not necessary for proj1
//              try {
//             PageId pid= page.getId();
//             HeapPageId hpid= (HeapPageId)pid;

//             RandomAccessFile rAf=new RandomAccessFile(this.file,"rw");
//             int offset = pid.pageNumber()*BufferPool.PAGE_SIZE;
//             byte[] b=new byte[BufferPool.PAGE_SIZE];
//             b=page.getPageData();
//             rAf.seek(offset);
//             rAf.write(b, 0, BufferPool.PAGE_SIZE);
//             rAf.close();          
//         }catch (IOException e){
//             e.printStackTrace();
//         }
//     }

//     /**
//      * Returns the number of pages in this HeapFile.
//      */
//     public int numPages() {
//         // some code goes here
//         //return 0;
//         //get the size of the file
//         long len = this.file.length();
//         int num = (int) (len / BufferPool.PAGE_SIZE);

//         return len % BufferPool.PAGE_SIZE <= 0 ? num : num + 1;
//     }

//     // see DbFile.java for javadocs
//     public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
//             throws DbException, IOException, TransactionAbortedException {
//         // some code goes here
//         // not necessary for proj1
//          ArrayList<Page> affectedPages = new ArrayList<Page>();
//         try{
//             if (this.getEmptyPages(tid).isEmpty()){
//                 HeapPageId hpid=new HeapPageId(this.getId(), this.numPages());
//                 HeapPage hp=new HeapPage(hpid, HeapPage.createEmptyPageData());
//                 hp.insertTuple(t);
//                 this.writePage(hp);
//                 affectedPages.add(hp);  

//             } else {
//                 Page p=this.getEmptyPages(tid).get(0);
//                 HeapPage hp=(HeapPage)p;
//                 hp.insertTuple(t);
//                 affectedPages.add(hp);       
//             }
//         }
//         catch (DbException e){
//                 e.printStackTrace();
//             }
//         catch (TransactionAbortedException e){
//                 e.printStackTrace();
//             }
//         catch (IOException e){
//             e.printStackTrace();
//         }
//             return affectedPages; 
//     }

//     // see DbFile.java for javadocs
//     public Page deleteTuple(TransactionId tid, Tuple t) throws DbException,
//             TransactionAbortedException {
//         // some code goes here
//             RecordId rid=t.getRecordId();
//         PageId pid=rid.getPageId();
//         Page p=Database.getBufferPool().getPage(tid,pid,Permissions.READ_WRITE);
//         HeapPage hp=(HeapPage)p;
//         hp.deleteTuple(t);
//         return Database.getBufferPool().getPage(tid,pid,Permissions.READ_ONLY);
//         // not necessary for proj1
//     }

//     public ArrayList<Page> getEmptyPages(TransactionId tid) throws DbException,TransactionAbortedException{
//         ArrayList<Page> pagelist=new ArrayList<Page>();
//         try{
//             int tableid=this.getId();
//             for (int i=0; i<this.numPages();i++){
//                 HeapPageId pid= new HeapPageId(tableid,i);
//                 Page page = Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
//                 if (((HeapPage)page).getNumEmptySlots()!=0){
//                     pagelist.add(page);
//                 }
//             }
//         } 
//         catch (DbException e){
//                 e.printStackTrace();
//             }
//         catch (TransactionAbortedException e){
//                 e.printStackTrace();
//             }
//             return pagelist;
//     }

//     // see DbFile.java for javadocs
//     public DbFileIterator iterator(TransactionId tid) {
//         // some code goes here
//         //return null;
//         int totalPage = numPages();
//         List<Tuple> tuples = new ArrayList<Tuple>();

//         for (int i = 0; i < totalPage; i++) {
//             HeapPage page = null;
//             PageId pid = new HeapPageId(this.heapFileId, i);
//             try {
//                 page = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
//             } catch(TransactionAbortedException e) {
//                 e.printStackTrace();
//                 System.exit(0);
//             } catch(DbException e) {
//                 e.printStackTrace();
//                 System.exit(0);
//             }
//             tuples.addAll(page.getAllTuples());
//         }

//         return new HeapFileIterator(tuples);
//     }

// }

package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {
    
    //file (all HeapPages)
    File DFfile;
    //table schema
    TupleDesc DFtd;
    

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.DFfile = f;
        this.DFtd = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return this.DFfile;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        return this.DFfile.getAbsoluteFile().hashCode();
        //throw new UnsupportedOperationException("implement this");
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.DFtd;
        //throw new UnsupportedOperationException("implement this");
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        HeapPageId hpid = (HeapPageId) pid;
        long offset = BufferPool.PAGE_SIZE * hpid.pageNumber();
        byte[] tbuffer = new byte[BufferPool.PAGE_SIZE];
        
        try{
            BufferedInputStream fin = new BufferedInputStream(new FileInputStream(this.DFfile));
            fin.skip(offset);
            fin.read(tbuffer, 0, BufferPool.PAGE_SIZE);
            
            try{
                fin.close();
            } catch(IOException E){
                //DO NOTHIGN
            }
            
            return new HeapPage(hpid, tbuffer);
        } catch(IOException e){
            return null;
        }
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for proj1
        RandomAccessFile randomWriter = new RandomAccessFile(this.DFfile, "rw");
        randomWriter.seek(page.getId().pageNumber() * BufferPool.PAGE_SIZE);
        byte[] data = page.getPageData();
        randomWriter.write(data);
        randomWriter.close();
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int)this.DFfile.length() / BufferPool.PAGE_SIZE;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        //return null;
        // not necessary for proj1
        ArrayList<Page> pageList = new ArrayList<Page>();
        
        for(int i = 0; i < this.numPages(); i++){
            PageId pid = new HeapPageId(this.getId(), i);
            HeapPage p = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
            
            //if there is space, tuple can be inserted
            if(p.getNumEmptySlots() > 0){
                //System.out.print("find avaliable page");
                p.insertTuple(t);
                pageList.add(p);
                return pageList;
            }
        }
        
        //out of loop, no available page, create a new page
        HeapPageId newpid = new HeapPageId(this.getId(), this.numPages());
        HeapPage newp = new HeapPage(newpid, HeapPage.createEmptyPageData());
        //newp.insertTuple(t);
        this.writePage(newp);
        HeapPage tempp = (HeapPage)Database.getBufferPool().getPage(tid, newpid, Permissions.READ_WRITE);
        tempp.insertTuple(t);
        pageList.add(tempp);
        return pageList;
        
    }

    // see DbFile.java for javadocs
    public Page deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        //return null;
        // not necessary for proj1
        HeapPage dp;
        PageId pid = t.getRecordId().getPageId();
        
        dp = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
        dp.deleteTuple(t);
        return dp;
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        
        return new DFIterator(this, tid);
    }
    
    
    class DFIterator implements DbFileIterator{
        HeapFile hf;
        TransactionId transid;
        int PageIndex;
        //int TupleIndex;
        Iterator<Tuple> TupleIndex;
        HeapPage currentHP;
        
        
        public DFIterator(HeapFile hf, TransactionId transid){
            this.hf = hf;
            this.transid = transid;
            //ensure that without open(), hasNext() will return false
            //so next() will throw exception
            PageIndex = hf.numPages() + 1;
            TupleIndex = null;
            currentHP = null;
            //System.out.print("DFIterator init ready\n");
        }
        
        public void open(){
            PageIndex = -1;
            TupleIndex = null;
            currentHP = null;
        }
        
        //get a tuple iterator of this page (page of PageIndex)
        //set this.TupleIndex to the iterator
        public void getTupleITofPage() {   
            HeapPageId thpid = new HeapPageId(hf.getId(), PageIndex);
            try{
                currentHP = (HeapPage)Database.getBufferPool().getPage(transid, thpid, Permissions.READ_ONLY);
            } catch(DbException dbe){
                    //DO NOTHING
                System.out.print("DbException\n");
            } catch(TransactionAbortedException tae){
                    //DO NOTHING
                System.out.print("TransactionAbortedException\n");
            }
            this.TupleIndex = currentHP.iterator();
        }
        
        public boolean hasNext(){
            if(PageIndex >= hf.numPages())
                return false;
            
            if(TupleIndex != null && TupleIndex.hasNext())
                return true;
            else{
                //try to get a valid iterator
                while(true){
                    PageIndex++;
                    //all page has been scanned
                    if(PageIndex >= hf.numPages())
                        break;
                    getTupleITofPage();
                    if(this.TupleIndex != null && this.TupleIndex.hasNext())
                        break;
                }
                //no more pages, (the last PageIndex is hf.numPages() - 1)
                if(PageIndex >= hf.numPages())
                    return false;
                else
                    return true;
            }
        }
        
        public Tuple next(){
            if(this.hasNext()){
                return TupleIndex.next();
            }
            else{
                throw new NoSuchElementException();
            }
        }
        
        public void rewind(){
            this.close();
            this.open();
        }
        
        public void close(){
            TupleIndex = null;
            PageIndex = hf.numPages() + 1;
        }
    }

}