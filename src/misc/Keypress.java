package misc;
/*
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//THIS IS AN EXAMPLE. IT IS NOT USED IN THE GAME CURRENTLY
public class Keypress {

    public static void main(String[] args) {
        new Keypress();
    }

    public Keypress() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        /**
		 * 
		 *//*
		private static final long serialVersionUID = 1L;
		private Delta xDelta;
        private Delta yDelta;

        public TestPane() {
            xDelta = new Delta();
            yDelta = new Delta();
            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.up.pressed", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new DeltaAction(yDelta, -5));
            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.up.released", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), new DeltaAction(yDelta));
            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.down.pressed", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new DeltaAction(yDelta, 5));
            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.down.released", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), new DeltaAction(yDelta));

            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.left.pressed", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new DeltaAction(xDelta, -5));
            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.left.released", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), new DeltaAction(xDelta));
            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.right.pressed", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new DeltaAction(xDelta, 5));
            bindKeyStrokeTo(WHEN_IN_FOCUSED_WINDOW, "delta.right.released", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), new DeltaAction(xDelta));

            Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Window window = SwingUtilities.getWindowAncestor(TestPane.this);
                    Dimension size = window.getSize();
                    size.width += xDelta.getValue();
                    size.height += yDelta.getValue();
                    window.setSize(size);
                }
            });
            timer.start();
        }

        public void bindKeyStrokeTo(int condition, String name, KeyStroke keyStroke, Action action) {
            InputMap im = getInputMap(condition);
            ActionMap am = getActionMap();

            im.put(keyStroke, name);
            am.put(name, action);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

    }

    public class Delta {

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

    }

    public class DeltaAction extends AbstractAction {

        /**
		 * 
		 */ /*
		private static final long serialVersionUID = 1L;
		private Delta delta;
        private int value;

        public DeltaAction(Delta delta, int value) {
            this.delta = delta;
            this.value = value;
        }

        public DeltaAction(Delta delta) {
            this(delta, 0);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            delta.setValue(value);
        }

    }

}
*/
