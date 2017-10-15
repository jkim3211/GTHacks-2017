import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;
import java.awt.Dimension;
import java.awt.Robot;
import java.io.IOException;
import java.awt.event.InputEvent;


class LeapListener extends Listener {
	public Robot robot;

	public void onInIt(Controller controller) {
		System.out.println("Initializing");
	}
	public void onConnect(Controller controller) {
		System.out.println("Connected to Motion Sensor");
		controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
		controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);	
	}
	
	public void onDisconnect(Controller controller) {
		System.out.println("Motion Sensor Disconnected");
	}
	public void onExit(Controller controller) {
		System.out.println("Exited");
	}
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		
		try {
			robot = new Robot();
		} catch (Exception e) {
			e.printStackTrace();
		}
		InteractionBox box = frame.interactionBox();
		for (Finger f: frame.fingers()) {
			if (f.type() == Finger.Type.TYPE_INDEX) {
				Vector fingerPos = f.stabilizedTipPosition();
				Vector boxFingerPos = box.normalizePoint(fingerPos);
				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				robot.mouseMove((int) (screen.width * boxFingerPos.getX()), (int) (screen.height - boxFingerPos.getY() * screen.height));
			}
		}
		
		GestureList gestures = frame.gestures();
		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);
			if (gesture.type() == Type.TYPE_KEY_TAP) {
				robot.mousePress(InputEvent.BUTTON1_MASK); 
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			} 
		}
	}
}

public class LeapController {

	public static void main(String[] args) {
		LeapListener listener = new LeapListener();
		Controller controller = new Controller();
		controller.addListener(listener);
		System.out.println("Press enter to quit");
		try {
			System.in.read();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		controller.removeListener(listener);
	}
}
