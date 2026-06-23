// StepMetronome.java - Степ-метроном на Java (Swing)
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;

public class StepMetronome extends JFrame {
    private int bpm = 120;
    private boolean running = false;
    private Timer timer;
    private int beatCount = 0;
    private boolean accent = true;

    private JLabel bpmLabel;
    private JSlider bpmSlider;
    private JButton startBtn, stopBtn;
    private JLabel statusLabel;
    private JPanel indicatorPanel;
    private JCheckBox accentCheck;

    public StepMetronome() {
        setTitle("🎵 StepMetronome - Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Верхняя панель
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(44, 62, 80));
        top.setPreferredSize(new Dimension(0, 80));
        JLabel title = new JLabel("🎵 Степ-метроном", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Центр
        JPanel center = new JPanel();
        center.setBackground(new Color(44, 62, 80));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // BPM дисплей
        bpmLabel = new JLabel("120 BPM", SwingConstants.CENTER);
        bpmLabel.setFont(new Font("Courier New", Font.BOLD, 48));
        bpmLabel.setForeground(new Color(241, 196, 15));
        bpmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(Box.createVerticalStrut(10));
        center.add(bpmLabel);

        // Ползунок BPM
        bpmSlider = new JSlider(30, 240, 120);
        bpmSlider.setBackground(new Color(44, 62, 80));
        bpmSlider.setMajorTickSpacing(30);
        bpmSlider.setMinorTickSpacing(10);
        bpmSlider.setPaintTicks(true);
        bpmSlider.setPaintLabels(true);
        bpmSlider.setForeground(Color.WHITE);
        bpmSlider.addChangeListener(e -> {
            bpm = bpmSlider.getValue();
            bpmLabel.setText(bpm + " BPM");
            if (running) {
                stopMetronome();
                startMetronome();
            }
        });
        center.add(bpmSlider);

        // Кнопки BPM
        JPanel bpmBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        bpmBtns.setOpaque(false);
        int[] deltas = {-10, -1, 1, 10};
        for (int d : deltas) {
            JButton btn = new JButton((d > 0 ? "+" : "") + d);
            btn.setBackground(new Color(52, 73, 94));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> changeBpm(d));
            bpmBtns.add(btn);
        }
        center.add(bpmBtns);

        // Индикатор
        indicatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        indicatorPanel.setPreferredSize(new Dimension(120, 120));
        indicatorPanel.setMaximumSize(new Dimension(120, 120));
        indicatorPanel.setBackground(new Color(52, 73, 94));
        indicatorPanel.setBorder(BorderFactory.createLineBorder(new Color(127, 140, 141), 4));
        indicatorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(Box.createVerticalStrut(15));
        center.add(indicatorPanel);

        // Кнопки управления
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setOpaque(false);
        startBtn = new JButton("▶ Старт");
        startBtn.setBackground(new Color(46, 204, 113));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFont(new Font("Arial", Font.BOLD, 14));
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> toggleStartStop());
        controlPanel.add(startBtn);

        stopBtn = new JButton("⏹ Стоп");
        stopBtn.setBackground(new Color(231, 76, 60));
        stopBtn.setForeground(Color.WHITE);
        stopBtn.setFont(new Font("Arial", Font.BOLD, 14));
        stopBtn.setFocusPainted(false);
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(e -> stopMetronome());
        controlPanel.add(stopBtn);

        center.add(controlPanel);

        // Акцент
        JPanel accentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        accentPanel.setOpaque(false);
        accentCheck = new JCheckBox("Акцент на первой доле");
        accentCheck.setForeground(Color.WHITE);
        accentCheck.setBackground(new Color(44, 62, 80));
        accentCheck.setSelected(true);
        accentCheck.addActionListener(e -> accent = accentCheck.isSelected());
        accentPanel.add(accentCheck);
        center.add(accentPanel);

        // Статус
        statusLabel = new JLabel("Готов", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(189, 195, 199));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(Box.createVerticalStrut(5));
        center.add(statusLabel);

        add(center, BorderLayout.CENTER);

        // Клавиши
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggle");
        getRootPane().getActionMap().put("toggle", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { toggleStartStop(); }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "bpmUp");
        getRootPane().getActionMap().put("bpmUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { changeBpm(1); }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "bpmDown");
        getRootPane().getActionMap().put("bpmDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { changeBpm(-1); }
        });

        setVisible(true);
    }

    private void changeBpm(int delta) {
        int newBpm = Math.max(30, Math.min(240, bpm + delta));
        bpmSlider.setValue(newBpm);
        bpm = newBpm;
        bpmLabel.setText(bpm + " BPM");
        if (running) {
            stopMetronome();
            startMetronome();
        }
    }

    private void toggleStartStop() {
        if (running) {
            stopMetronome();
        } else {
            startMetronome();
        }
    }

    private void startMetronome() {
        if (running) return;
        running = true;
        beatCount = 0;
        startBtn.setText("⏸ Пауза");
        startBtn.setBackground(new Color(243, 156, 18));
        stopBtn.setEnabled(true);
        statusLabel.setText("Идёт...");
        timer = new Timer((int)(60000.0 / bpm), e -> beat());
        timer.start();
    }

    private void stopMetronome() {
        running = false;
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        startBtn.setText("▶ Старт");
        startBtn.setBackground(new Color(46, 204, 113));
        stopBtn.setEnabled(false);
        statusLabel.setText("Остановлено");
        indicatorPanel.setBackground(new Color(52, 73, 94));
    }

    private void beat() {
        boolean isAccent = accent && (beatCount % 4 == 0);
        // Визуализация
        Color flashColor = isAccent ? new Color(241, 196, 15) : new Color(46, 204, 113);
        indicatorPanel.setBackground(flashColor);
        // Звук
        playSound(isAccent);
        beatCount++;
        // Возврат цвета через 100 мс
        new Thread(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            if (running) {
                SwingUtilities.invokeLater(() -> indicatorPanel.setBackground(new Color(52, 73, 94)));
            }
        }).start();
    }

    private void playSound(boolean accent) {
        try {
            float frequency = accent ? 880 : 660;
            float duration = accent ? 0.08f : 0.05f;
            AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
            int samples = (int)(format.getSampleRate() * duration);
            byte[] buf = new byte[samples];
            for (int i = 0; i < samples; i++) {
                double angle = 2.0 * Math.PI * frequency * i / format.getSampleRate();
                buf[i] = (byte)(Math.sin(angle) * 127);
            }
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buf, 0, buf.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StepMetronome::new);
    }
}
