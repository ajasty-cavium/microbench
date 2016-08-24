 import java.util.*;

 public class HashCodeTest
 {
   static int ITS = 10000000;
     static Object O = new Object();

	   public static void main(String[] args)
	     {
		     TreeSet<Integer> large = new TreeSet<Integer>();
			     Random ran = new Random();
				     for (int i=0; i<1000000; i++) {
					       large.add(ran.nextInt());
						       }

							       testIdentityHashCode(large);
								       testHashCode(large);
									       testIdentityHashCode(large);
										       testHashCode(large);
											     }

												   protected static void testIdentityHashCode(TreeSet<Integer> large)
												     {
													     long start = System.nanoTime();
														     for (int i=0; i<ITS; i++) {
															       Object o = new Container(large);
																         System.identityHashCode(o);
																		     }
																			     long end = System.nanoTime();
																				     System.out.println("System.identityHashCode: " + (end-start) + " - " + (end-start)/ITS + "ns");
																					   }

																					     protected static void testHashCode(TreeSet<Integer> large)
																						   {
																						       long start = System.nanoTime();
																							       for (int i=0; i<ITS; i++) {
																								         Object o = new Container(large);
																										       o.hashCode();
																											       }
																												       long end = System.nanoTime();

																													       System.out.println("Object.hashCode: " + (end-start) + " - " + (end-start)/ITS + "ns");
																														     }

																															   private static class Container {
																															       private final Object o;

																																       public Container(Object o)
																																	       {
																																		         this.o = o;
																																				     }
																																					   }
}
