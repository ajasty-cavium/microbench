import java.util.*;
public class LinkedListExample {
     public static void main(String args[]) {

         /* Linked List Declaration */
         LinkedList<Integer> linkedlist = new LinkedList<Integer>();

         /*add(String Element) is used for adding 
          * the elements to the linked list*/

         /*Display Linked List Content*/
         System.out.println("Linked List Content: " +linkedlist);

         /*Add First and Last Element*/

         /*This is how to get and set Values*/
         System.out.println("LinkedList after deletion of first and last element: " +linkedlist);
long start_time = System.nanoTime();

         /* Add to a Position and remove from a position*/
	 Random rnd = new Random();

 	for(int i=0;i<10000000;++i)
	{
		linkedlist.add(rnd.nextInt());
	}
long end_time = System.nanoTime();
double difference = (end_time - start_time)/1e6;

               System.out.println("time was " +difference);

  int el=0;
  int sum=0;
  for(int i=0;i<10000000;++i)
        {
              el= linkedlist.pop();	
              sum =(sum+el)%10;
        }

 start_time = System.nanoTime();
 difference = (end_time - start_time)/1e6;
 System.out.println("time was " +difference);

     }
}
