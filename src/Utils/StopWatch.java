package Utils;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.io.input.CountingInputStream;

public class StopWatch {
    
    private static long startTime = 0;
    private static long lastSnapshotTime = 0;
    private static long stopTime = 0;

    private static double startCOScount = 0;
    private static double stopCOScount = 0;
    private static double lastCOScount  = 0;
    private static double startCIScount = 0;
    private static double stopCIScount = 0;
    private static double lastCIScount  = 0;
    public static CountingOutputStream cos = null;
    public static CountingInputStream  cis = null;

    public static void start() {
        lastSnapshotTime = startTime = System.currentTimeMillis();

	if (cos != null)
	    lastCOScount = startCOScount = cos.getByteCount() / 1024.0;
	if (cis != null)
	    lastCIScount = startCIScount = cis.getByteCount() / 1024.0;

	System.out.println("Program starting time (ms): " + startTime + " (" + startCOScount + ", " + startCIScount + ")");
    }
    
    public static void stop() {
        lastSnapshotTime = stopTime = System.currentTimeMillis();

	if (cos != null)
	    lastCOScount = startCOScount = cos.getByteCount() / 1024.0;
	if (cis != null)
	    lastCIScount = startCIScount = cis.getByteCount() / 1024.0;

	System.out.println("Program stopping time (ms): " + stopTime + " (" + stopCOScount + ", " + stopCIScount + ")");
    }

    public static void pointTimeStamp(String point) {
	System.out.println("Time (ms) " + point + ": " + getElapsedTime() + " (" + getOutputCounter() + ", " + getInputCounter() + ")");
    }

    public static void taskTimeStamp(String task) {
	System.out.println("Elapsed time (ms) on " + task + ": " + getSegmentedElapsedTime() + " (" + getOutputUsage() + ", " + getInputUsage() + ")");
    }

    public static long getElapsedTime() {
        long elapsed;
	lastSnapshotTime = System.currentTimeMillis();
	elapsed = lastSnapshotTime - startTime;

        return elapsed;
    }

    public static double getOutputCounter() {
	if (cos == null)
	    return 0;

	lastCOScount = cos.getByteCount() / 1024.0;
	return lastCOScount - startCOScount;
    }

    public static double getInputCounter() {
	if (cis == null)
	    return 0;

	lastCIScount = cis.getByteCount() / 1024.0;
	return lastCIScount - startCIScount;
    }

    public static long getSegmentedElapsedTime() {
        long elapsed;
	long snapshotTime = System.currentTimeMillis();
	elapsed = snapshotTime - lastSnapshotTime;
	lastSnapshotTime = snapshotTime;

        return elapsed;
    }

    public static double getOutputUsage() {
	if (cos == null)
	    return 0;

	double used;
	double currentCount = cos.getByteCount() / 1024.0;
	used = currentCount - lastCOScount;
	lastCOScount = currentCount;

	return used;
    }

    public static double getInputUsage() {
	if (cis == null)
	    return 0;

	double used;
	double currentCount = cis.getByteCount() / 1024.0;
	used = currentCount - lastCIScount;
	lastCIScount = currentCount;

	return used;
    }
}