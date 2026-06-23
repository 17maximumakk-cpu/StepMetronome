// StepMetronome.cs - Степ-метроном на C# (WinForms)
using System;
using System.Drawing;
using System.Windows.Forms;
using System.Threading;

public class StepMetronome : Form
{
    private int bpm = 120;
    private bool running = false;
    private System.Windows.Forms.Timer timer;
    private int beatCount = 0;
    private bool accent = true;

    private Label bpmLabel;
    private TrackBar bpmSlider;
    private Button startBtn, stopBtn;
    private Label statusLabel;
    private Panel indicatorPanel;
    private CheckBox accentCheck;

    public StepMetronome()
    {
        Text = "🎵 StepMetronome - C#";
        Size = new Size(500, 450);
        StartPosition = FormStartPosition.CenterScreen;
        FormBorderStyle = FormBorderStyle.FixedSingle;
        MaximizeBox = false;
        BackColor = Color.FromArgb(44, 62, 80);

        // Заголовок
        Label title = new Label
        {
            Text = "🎵 Степ-метроном",
            Font = new Font("Arial", 24, FontStyle.Bold),
            ForeColor = Color.White,
            Dock = DockStyle.Top,
            Height = 60,
            TextAlign = ContentAlignment.MiddleCenter,
            BackColor = Color.FromArgb(44, 62, 80)
        };
        Controls.Add(title);

        // BPM дисплей
        bpmLabel = new Label
        {
            Text = "120 BPM",
            Font = new Font("Courier New", 40, FontStyle.Bold),
            ForeColor = Color.Gold,
            BackColor = Color.FromArgb(44, 62, 80),
            Dock = DockStyle.Top,
            Height = 70,
            TextAlign = ContentAlignment.MiddleCenter
        };
        Controls.Add(bpmLabel);

        // Ползунок BPM
        bpmSlider = new TrackBar
        {
            Minimum = 30,
            Maximum = 240,
            Value = 120,
            TickFrequency = 10,
            TickStyle = TickStyle.Both,
            BackColor = Color.FromArgb(44, 62, 80),
            ForeColor = Color.White,
            Dock = DockStyle.Top,
            Height = 60
        };
        bpmSlider.ValueChanged += (s, e) => {
            bpm = bpmSlider.Value;
            bpmLabel.Text = bpm + " BPM";
            if (running) { StopMetronome(); StartMetronome(); }
        };
        Controls.Add(bpmSlider);

        // Кнопки BPM
        FlowLayoutPanel bpmBtns = new FlowLayoutPanel
        {
            Dock = DockStyle.Top,
            Height = 40,
            BackColor = Color.FromArgb(44, 62, 80),
            FlowDirection = FlowDirection.LeftToRight,
            Padding = new Padding(50, 5, 50, 5)
        };
        int[] deltas = { -10, -1, 1, 10 };
        foreach (int d in deltas)
        {
            Button btn = new Button
            {
                Text = (d > 0 ? "+" : "") + d,
                BackColor = Color.FromArgb(52, 73, 94),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Width = 50,
                Height = 30
            };
            btn.Click += (s, e) => ChangeBpm(d);
            bpmBtns.Controls.Add(btn);
        }
        Controls.Add(bpmBtns);

        // Индикатор
        indicatorPanel = new Panel
        {
            Size = new Size(120, 120),
            BackColor = Color.FromArgb(52, 73, 94),
            Dock = DockStyle.Top,
            Padding = new Padding(0),
            Margin = new Padding(0)
        };
        indicatorPanel.Paint += (s, e) => {
            Graphics g = e.Graphics;
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;
            g.FillEllipse(new SolidBrush(indicatorPanel.BackColor), 0, 0, 120, 120);
            g.DrawEllipse(new Pen(Color.FromArgb(127, 140, 141), 4), 2, 2, 116, 116);
        };
        Controls.Add(indicatorPanel);

        // Кнопки управления
        FlowLayoutPanel controlPanel = new FlowLayoutPanel
        {
            Dock = DockStyle.Top,
            Height = 60,
            BackColor = Color.FromArgb(44, 62, 80),
            FlowDirection = FlowDirection.LeftToRight,
            Padding = new Padding(80, 10, 80, 10)
        };
        startBtn = new Button
        {
            Text = "▶ Старт",
            BackColor = Color.FromArgb(46, 204, 113),
            ForeColor = Color.White,
            FlatStyle = FlatStyle.Flat,
            Font = new Font("Arial", 12, FontStyle.Bold),
            Width = 100,
            Height = 40
        };
        startBtn.Click += (s, e) => ToggleStartStop();
        controlPanel.Controls.Add(startBtn);

        stopBtn = new Button
        {
            Text = "⏹ Стоп",
            BackColor = Color.FromArgb(231, 76, 60),
            ForeColor = Color.White,
            FlatStyle = FlatStyle.Flat,
            Font = new Font("Arial", 12, FontStyle.Bold),
            Width = 100,
            Height = 40,
            Enabled = false
        };
        stopBtn.Click += (s, e) => StopMetronome();
        controlPanel.Controls.Add(stopBtn);
        Controls.Add(controlPanel);

        // Акцент
        FlowLayoutPanel accentPanel = new FlowLayoutPanel
        {
            Dock = DockStyle.Top,
            Height = 40,
            BackColor = Color.FromArgb(44, 62, 80),
            FlowDirection = FlowDirection.LeftToRight,
            Padding = new Padding(120, 5, 120, 5)
        };
        accentCheck = new CheckBox
        {
            Text = "Акцент на первой доле",
            ForeColor = Color.White,
            BackColor = Color.FromArgb(44, 62, 80),
            Checked = true,
            AutoSize = true
        };
        accentCheck.CheckedChanged += (s, e) => accent = accentCheck.Checked;
        accentPanel.Controls.Add(accentCheck);
        Controls.Add(accentPanel);

        // Статус
        statusLabel = new Label
        {
            Text = "Готов",
            ForeColor = Color.FromArgb(189, 195, 199),
            BackColor = Color.FromArgb(44, 62, 80),
            Dock = DockStyle.Top,
            Height = 30,
            TextAlign = ContentAlignment.MiddleCenter,
            Font = new Font("Arial", 10)
        };
        Controls.Add(statusLabel);

        // Клавиши
        KeyPreview = true;
        KeyDown += (s, e) => {
            if (e.KeyCode == Keys.Space) { e.SuppressKeyPress = true; ToggleStartStop(); }
            if (e.KeyCode == Keys.Up) { e.SuppressKeyPress = true; ChangeBpm(1); }
            if (e.KeyCode == Keys.Down) { e.SuppressKeyPress = true; ChangeBpm(-1); }
        };
    }

    private void ChangeBpm(int delta)
    {
        int newBpm = Math.Max(30, Math.Min(240, bpm + delta));
        bpmSlider.Value = newBpm;
        bpm = newBpm;
        bpmLabel.Text = bpm + " BPM";
        if (running) { StopMetronome(); StartMetronome(); }
    }

    private void ToggleStartStop()
    {
        if (running) StopMetronome();
        else StartMetronome();
    }

    private void StartMetronome()
    {
        if (running) return;
        running = true;
        beatCount = 0;
        startBtn.Text = "⏸ Пауза";
        startBtn.BackColor = Color.FromArgb(243, 156, 18);
        stopBtn.Enabled = true;
        statusLabel.Text = "Идёт...";
        timer = new System.Windows.Forms.Timer();
        timer.Interval = (int)(60000.0 / bpm);
        timer.Tick += (s, e) => Beat();
        timer.Start();
    }

    private void StopMetronome()
    {
        running = false;
        if (timer != null) { timer.Stop(); timer = null; }
        startBtn.Text = "▶ Старт";
        startBtn.BackColor = Color.FromArgb(46, 204, 113);
        stopBtn.Enabled = false;
        statusLabel.Text = "Остановлено";
        indicatorPanel.BackColor = Color.FromArgb(52, 73, 94);
    }

    private void Beat()
    {
        bool isAccent = accent && (beatCount % 4 == 0);
        // Визуализация
        Color flashColor = isAccent ? Color.Gold : Color.FromArgb(46, 204, 113);
        indicatorPanel.BackColor = flashColor;
        // Звук
        PlaySound(isAccent);
        beatCount++;
        // Возврат цвета через 100 мс
        System.Threading.Timer resetTimer = null;
        resetTimer = new System.Threading.Timer(_ => {
            if (running) {
                indicatorPanel.Invoke(new Action(() => indicatorPanel.BackColor = Color.FromArgb(52, 73, 94)));
            }
            resetTimer?.Dispose();
        }, null, 100, Timeout.Infinite);
    }

    private void PlaySound(bool accent)
    {
        try
        {
            int freq = accent ? 880 : 660;
            int duration = accent ? 80 : 50;
            Console.Beep(freq, duration);
        }
        catch { /* Beep не поддерживается */ }
    }

    [STAThread]
    static void Main()
    {
        Application.EnableVisualStyles();
        Application.Run(new StepMetronome());
    }
}
