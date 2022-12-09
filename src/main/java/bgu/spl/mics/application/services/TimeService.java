package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int time;
	public static int speed;
	int duration;
	public Cluster cluster;

	public TimeService(int speed, int duration) {
		super("Time");
		// TODO Implement this
		this.speed=speed;
		this.duration=duration;
		cluster=Cluster.getInstance();
		time=0;
	}

	@Override
	protected void initialize() {
		register();

		subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
			@Override
			public void call(TerminateAllBroadcast c) {
				terminate();
			}
		});
		Timer timer = new Timer();


		CRMSRunner.countDownLatch.countDown();
		try {
			CRMSRunner.countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
			time++;
			TickBroadcast tick = new TickBroadcast(time);
			sendBroadcast(tick);

			if(time==duration)
				{
					TerminateAllBroadcast terminateAll = new TerminateAllBroadcast();
					sendBroadcast(terminateAll);
					cancel();
				}
			}
		}, speed, speed);

		}



		// TODO Implement this

	}








