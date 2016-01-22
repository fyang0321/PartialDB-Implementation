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
    private File file = null;
    private TupleDesc tupleDesc = null;
    private int heapFileId;
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        this.file = f;
        this.tupleDesc = td;
        this.heapFileId = this.file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return this.file;
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
        //throw new UnsupportedOperationException("implement this");
        return this.heapFileId;
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
        return this.tupleDesc;
    }

    //TODO: change
    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        //return null;
        // Page newPage = null;
        // try {
        //     RandomAccessFile randomReader = new RandomAccessFile(this.file, "r");
        //     //adjust pointer to right offset;
        //     randomReader.seek(pid.pageNumber() * BufferPool.PAGE_SIZE);
        //     byte[] data = new byte[BufferPool.PAGE_SIZE];
        //     randomReader.read(data, 0, BufferPool.PAGE_SIZE);
        //     randomReader.close();

        //     newPage = new HeapPage((HeapPageId)pid, data);
        // } catch (FileNotFoundException e) {
        //     e.printStackTrace();
        //     System.exit(0);
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     System.exit(0);
        // }

        // return newPage;
          try{

            RandomAccessFile rAf=new RandomAccessFile(file,"r");
            int offset = pid.pageNumber()*BufferPool.PAGE_SIZE;
            byte[] b=new byte[BufferPool.PAGE_SIZE];
            rAf.seek(offset);
            rAf.read(b, 0, BufferPool.PAGE_SIZE);
            HeapPageId hpid=(HeapPageId)pid;
            rAf.close();  

            return new HeapPage(hpid, b);         
            }catch (IOException e){
                e.printStackTrace();
            }
        throw new IllegalArgumentException();
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        //return 0;
        //get the size of the file
        long len = this.file.length();
        int num = (int) (len / BufferPool.PAGE_SIZE);

        return len % BufferPool.PAGE_SIZE <= 0 ? num : num + 1;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for proj1
    }

    // see DbFile.java for javadocs
    public Page deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for proj1
    }

    //TODO: do not understand
    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        //return null;
        int totalPage = numPages();
        List<Tuple> tuples = new ArrayList<Tuple>();

        for (int i = 0; i < totalPage; i++) {
            HeapPage page = null;
            PageId pid = new HeapPageId(this.heapFileId, i);
            try {
                page = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
            } catch(TransactionAbortedException e) {
                e.printStackTrace();
                System.exit(0);
            } catch(DbException e) {
                e.printStackTrace();
                System.exit(0);
            }
            //tuples.addAll(page.getAllTuples());
            //todo change!!!
            Iterator<Tuple> it = page.iterator();
            while (it.hasNext())
                tuples.add(it.next());
        }

        return new HeapFileIterator(tuples);
    }

}