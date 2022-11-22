package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private static final String UP_NAME = "up";
    private static final String DOWN_NAME = "down";
    private static final String STOP_NAME = "stop";

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;

    private final JLabel display = new JLabel();
    private final JButton up = new JButton(UP_NAME);
    private final JButton down = new JButton(DOWN_NAME);
    private final JButton stop = new JButton(STOP_NAME);

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        up.addActionListener((e) -> agent.setDown(false));
        down.addActionListener((e) -> agent.setDown(true));
        stop.addActionListener((e) -> {
            agent.stopCounting();
            disableButtons();
        });
    }

    private void disableButtons() {
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }

    private class Agent implements Runnable {
        private static final long COUNT_MS = 100;
        private volatile boolean stop;
        private volatile boolean down;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    if (!this.down) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(COUNT_MS);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();   // NOPMD suppressed as it is an exercise
                }
            }
        }

        /**
         * External command to set counting direction.
         * @param isDecrease set counter to decrease
         */
        public void setDown(final boolean isDecrease) {
            this.down = isDecrease;
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }
    }
}
