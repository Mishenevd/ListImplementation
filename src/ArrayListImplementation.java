import java.io.Serializable;
import java.util.*;

public class ArrayListImplementation<E> implements Serializable, Cloneable, Iterable<E>, Collection<E>, List<E>, RandomAccess {

    private  E [] values; /* array of elements */
    private int emptyElementPointer;
    {
        emptyElementPointer = 0;
    }

    public ArrayListImplementation () {

        values = (E[]) new Object [10] ;
    }

    public ArrayListImplementation( int initialCapacity) {

        values = (E[]) new Object [initialCapacity] ;
    }

    public ArrayListImplementation(Collection< ? extends E> collection) {

        int initialCapacity = collection.size() << 1;
        values = (E[]) new Object [initialCapacity];
    }

    private ArrayListImplementation(E[] values) {

        this.values = values;
    }


    @Override
    public boolean addAll(int index, Collection<? extends  E> c) {

        prepareArray(c);
        final int lastIndex = values.length-1;
        final int collectionSize = c.size();
        final int [] pointer = new int [] {index};
            c.forEach(element -> {
                /* need to swap element */
                if( pointer[0] <= lastIndex) {
                    E swap = values[pointer[0]];
                    values[pointer[0]] = element;
                    values[pointer[0] + collectionSize] = swap;
                }
                /* guaranteed empty elements -> no need to swap anything*/
                else{
                    values[pointer[0]] = (E) element;
                }
                /* do not forget to move pointer! */
                pointer[0] = pointer[0] + 1;
                emptyElementPointer++;
            });
        return true;
    }

    @Override
    public E get(int index) {
        if(index < size())
        return values[index];
        throw new IndexOutOfBoundsException();
    }

    @Override
    public E set(int index, E element) {
        values[index] =  element;
        return values[index];
    }

    @Override
    public void add(int index, E element) {
            if (values.length <= emptyElementPointer)
                resizeArray();
            int i;
            for(i=index; i< values.length - 1; i++)
                values[i+1] = values[i];
            values[emptyElementPointer] = element;
            /* do not forget to move pointer! */
            emptyElementPointer++;
    }

    @Override
    public E remove(int index) {
        Object objectToRemove = values[index];
        emptyElementPointer --;
        for(int i = index; i < values.length-1; i++){
            values[i] = values[i+1];
        }
        values[values.length-1] = null;
        return (E) objectToRemove;
    }

    @Override
    public int indexOf(Object o) {
        for(int i =0; i < emptyElementPointer; i++){
            if(values[i].equals(o))
                return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int foundIndex = -1;
        for(int i =0; i < emptyElementPointer; i++){
            if(values[i].equals(o))
                foundIndex = i;
        }
        return  foundIndex;
    }

    @Override
    public ListIterator<E> listIterator() {

        return new ListIteratorImpl();
    }

    @Override
    public ListIterator listIterator(int index) {
        if(index < 0 || (index > values.length-1))
            throw new IndexOutOfBoundsException();
        return new ListIteratorImpl(){
            private int leftBorder = index;
            {
                iterPointer = index-1;
            }

            @Override
            public boolean hasPrevious() {
                return (iterPointer - 1) >= leftBorder;
            }

            @Override
            public E previous() {
                if(!hasPrevious())
                    throw new NoSuchElementException();
                isModified = false;
                return values[--iterPointer];
            }

            @Override
            public int previousIndex() {
                if(hasPrevious())
                return iterPointer - 1;
                return -1;
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        E[] values = Arrays.copyOfRange(this.values, fromIndex, toIndex);
        return new ArrayListImplementation<E>(values);
    }

    @Override
    public int size() {
        return emptyElementPointer;
    }

    @Override
    public boolean isEmpty() {
        return (emptyElementPointer < 1)? true : false;
    }

    @Override
    public boolean contains(Object o) {
        for(int i =0; i < emptyElementPointer; i++) {
            if (values[i].equals(o))
                return true;
        }
        return false;
    }

    @Override
    public E[] toArray() {
        return values;
    }

    @Override
    public boolean add(E o) {
        add(emptyElementPointer, o);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if(index !=-1)
            remove(index);
        else
            return false;
        return true;
    }


    @Override
    public boolean addAll(Collection c) {
        final int [] currentPos = new int[] {emptyElementPointer};
        prepareArray(c);
        c.forEach(element -> {

            values[currentPos[0]++] = (E) element;
        });
        return true;
    }

    @Override
    public void clear() {
        for(int i=0; i< values.length; i++)
            values[i] = null;
        emptyElementPointer=0;
    }

    @Override
    public boolean retainAll(Collection c) {
        boolean[] isChanged =  {false};
        c.forEach( element -> {
            int i = indexOf(element);
            if(i == -1){
                remove(i);
                isChanged[0] = true;
            }
        });
        return isChanged[0];
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean[] isChanged =  {false};
        c.forEach( element -> {
            int i = indexOf(element);
            while(i != -1){
                remove(i);
                isChanged[0] = true;
                i = indexOf(element);
            }
        });
        return isChanged[0];
    }

    @Override
    public boolean containsAll(Collection c) {
        boolean[] isContainsAll = {true};
        c.forEach(element -> {
            if(!contains(element)){
                isContainsAll[0] = false;
                return;
            }
        });
        return isContainsAll[0];
    }

    @Override
    public E[] toArray(Object[] a) {
       if (a.length < values.length)
        return values;
       else
           for(int i=0; i<values.length; i++)
               a[i] = values[i];
           return (E[]) a;
    }

    @Override
    public Iterator iterator() {
        return new ListIteratorImpl();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayListImplementation<?> that = (ArrayListImplementation<?>) o;
        return emptyElementPointer == that.emptyElementPointer &&
                Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(emptyElementPointer);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    /*
     * void to add size of collection to backing array
     * of current ArrayListImplementation.
     * Piece of this code is used in add(Collection c) and
     * addAll(Collection c) methods.
     */
    private void prepareArray(Collection c) {
        int toAddSize = c.size();
        int arrayListImplementationSize = this.size();
        /* resizing our implementation*/
        int newSize = arrayListImplementationSize + toAddSize;
        values =  Arrays.copyOf(values,newSize);
    }

    private void  resizeArray(){
        Arrays.copyOf(values, values.length << 1);
    }

    /*
    * ListIterator implementation
    */
    protected class ListIteratorImpl implements  ListIterator<E>   {
        protected int iterPointer = -1; /* pointer on current iterating element */

        protected boolean isModified = true; /* if true -> throw IllegalStateException*/

        @Override
        public boolean hasNext() {
            return (iterPointer + 1) < emptyElementPointer;
        }

        @Override
        public E next() {
            if((iterPointer + 1)  == emptyElementPointer )
                throw new NoSuchElementException();
            isModified = false;
            return values[++iterPointer];
        }

        @Override
        public boolean hasPrevious() {
            return (iterPointer - 1) >= 0;
        }

        @Override
        public E previous() {
            if((iterPointer - 1)  < 0 )
                throw new NoSuchElementException();
            isModified = false;
            return values[--iterPointer];
        }

        @Override
        public int previousIndex() {
            return iterPointer - 1;
        }

        @Override
        public int nextIndex() {
            return iterPointer + 1;
        }



        @Override
        public void remove() {

            if(isModified)
                throw  new IllegalStateException();
            ArrayListImplementation.this.remove(iterPointer);
            iterPointer --;
            isModified = true;
        }

        @Override
        public void set(E o) {
            if(isModified)
                throw  new IllegalStateException();
            values[iterPointer] = (E) o;
        }

        @Override
        public void add(E o) {
            if(isModified)
                throw  new IllegalStateException();
            if(ArrayListImplementation.this.isEmpty()) {
                ArrayListImplementation.this.add(o);
            }
            else
                ArrayListImplementation.this.add(iterPointer-1, o);
            /* insertion before element -> need to increase pointer*/
            iterPointer++;

        }
    }
}
