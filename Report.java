public class Report {
	public static long start,end;
	public static String sep=","; 
	public static void start() {
		start = System.nanoTime();
	}
	public static void end() {
		end = System.nanoTime();
	}
	public static void report(String name, long its, String units) {
		report(name,its, units, "its/s");
	}
	public static void report(String name, long its, String units, String itunits) {
		double duration=end-start;
		double sduration = (end - start)/1e9;
		if (units.equals("s"))
			duration=sduration;
		if (units.equals("ms"))
			duration = (end - start)/1e6;
		if (units.equals("us"))
			duration = (end - start)/1e3;

		String res=String.format("%s%.2f%s",sep,duration,units);
		String itres="";
		if (its>1) res+=String.format("%s%.2f%s/it%s%.2f%s",sep,duration/its,units,sep,its/sduration,itunits);
		System.out.println(name + res + itres);
	}
	public static void report(String name) {
		report(name,1,"ms");
	}
	public static void report(String name, String units) {
		report(name,1, units);
	}
	public static void report(String name, long its) {
		report(name,its, "ms");
	}
}
