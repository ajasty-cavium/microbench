import java.util.*;
public class LinkedListExample {

    public static void main(String args[]) {
	Comparator<Integer> comparator = new Comparator<Integer>() {
		public int compare(Integer c1, Integer c2) {
			return c2 - c1; 
		}
	};

        /* Linked List Declaration */
        LinkedList<Integer> linkedlist = new LinkedList<Integer>();

        /*add(String Element) is used for adding
         * the elements to the linked list*/

        /*Display Linked List Content*/
        System.out.println("Linked List Content: " +linkedlist);

        /*Add First and Last Element*/

        /*This is how to get and set Values*/
        System.out.println("LinkedList after deletion of first and last element: " +linkedlist);
		Report.start();

        /* Add to a Position and remove from a position*/
        Random rnd = new Random();

        for (int i=0;i<10000000;++i)
        {
            linkedlist.add(rnd.nextInt());
        }
		Report.end();
		Report.report("Linked List<I>-add");

		Report.start();
		linkedlist.sort((left,right) -> Integer.compare(right,left));
		//Collections.sort(linkedlist,comparator);
		Report.end();
		Report.report("Linked List<I>-sort","s");
		
        int el=0;
        int sum=0;
		Report.start();
        for (int i=0;i<10000000;++i)
        {
            el= linkedlist.pop();
            sum =(sum+el)%10;
        }
		Report.end();
		Report.report("Linked List<I>-pop");

    }
}
