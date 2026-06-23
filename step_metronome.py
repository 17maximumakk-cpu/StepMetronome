#!/usr/bin/env python3
# step_metronome.py - Степ-метроном на Python (Tkinter)
import tkinter as tk
from tkinter import ttk
import threading
import time
import sys

# Попытка импорта звука
try:
    import winsound
    SOUND_AVAILABLE = True
except ImportError:
    SOUND_AVAILABLE = False
    print("winsound не доступен (только Windows). Звук отключён.")

class StepMetronome:
    def __init__(self, root):
        self.root = root
        self.root.title("🎵 StepMetronome - Python")
        self.root.geometry("500x400")
        self.root.resizable(False, False)
        self.root.configure(bg="#2c3e50")

        # Переменные состояния
        self.bpm = 120
        self.running = False
        self.thread = None
        self.interval = 60.0 / self.bpm
        self.accent = True
        self.beat_count = 0

        self.create_widgets()
        self.update_bpm_label()

    def create_widgets(self):
        # Заголовок
        title = tk.Label(self.root, text="🎵 Степ-метроном",
                         font=('Arial', 24, 'bold'),
                         bg="#2c3e50", fg="#ecf0f1")
        title.pack(pady=15)

        # BPM отображение
        self.bpm_label = tk.Label(self.root, text="120 BPM",
                                  font=('Arial', 32, 'bold'),
                                  bg="#2c3e50", fg="#f1c40f")
        self.bpm_label.pack(pady=10)

        # Ползунок BPM
        self.bpm_slider = tk.Scale(self.root, from_=30, to=240,
                                   orient=tk.HORIZONTAL,
                                   length=300, bg="#2c3e50",
                                   fg="white", highlightthickness=0,
                                   command=self.on_bpm_change)
        self.bpm_slider.set(120)
        self.bpm_slider.pack(pady=10)

        # Кнопки +/- BPM
        bpm_btn_frame = tk.Frame(self.root, bg="#2c3e50")
        bpm_btn_frame.pack(pady=5)
        tk.Button(bpm_btn_frame, text="-10", command=lambda: self.change_bpm(-10),
                  bg="#34495e", fg="white", width=5).pack(side=tk.LEFT, padx=5)
        tk.Button(bpm_btn_frame, text="-1", command=lambda: self.change_bpm(-1),
                  bg="#34495e", fg="white", width=5).pack(side=tk.LEFT, padx=5)
        tk.Button(bpm_btn_frame, text="+1", command=lambda: self.change_bpm(1),
                  bg="#34495e", fg="white", width=5).pack(side=tk.LEFT, padx=5)
        tk.Button(bpm_btn_frame, text="+10", command=lambda: self.change_bpm(10),
                  bg="#34495e", fg="white", width=5).pack(side=tk.LEFT, padx=5)

        # Визуальный индикатор
        self.indicator = tk.Canvas(self.root, width=100, height=100,
                                   bg="#2c3e50", highlightthickness=0)
        self.indicator.pack(pady=20)
        self.circle = self.indicator.create_oval(10, 10, 90, 90,
                                                  fill="#34495e",
                                                  outline="white", width=2)

        # Кнопки управления
        control_frame = tk.Frame(self.root, bg="#2c3e50")
        control_frame.pack(pady=10)
        self.start_btn = tk.Button(control_frame, text="▶ Старт",
                                   command=self.start_stop,
                                   bg="#2ecc71", fg="white",
                                   font=('Arial', 12), width=10)
        self.start_btn.pack(side=tk.LEFT, padx=5)

        self.stop_btn = tk.Button(control_frame, text="⏹ Стоп",
                                  command=self.stop,
                                  bg="#e74c3c", fg="white",
                                  font=('Arial', 12), width=10,
                                  state=tk.DISABLED)
        self.stop_btn.pack(side=tk.LEFT, padx=5)

        # Статус
        self.status_label = tk.Label(self.root, text="Готов",
                                     font=('Arial', 10),
                                     bg="#2c3e50", fg="#bdc3c7")
        self.status_label.pack(pady=5)

        # Чекбокс акцента
        self.accent_var = tk.BooleanVar(value=True)
        accent_check = tk.Checkbutton(self.root, text="Акцент на первой доле",
                                      variable=self.accent_var,
                                      bg="#2c3e50", fg="white",
                                      selectcolor="#2c3e50")
        accent_check.pack(pady=5)

        # Привязка клавиш
        self.root.bind("<space>", lambda e: self.start_stop())
        self.root.bind("<Up>", lambda e: self.change_bpm(1))
        self.root.bind("<Down>", lambda e: self.change_bpm(-1))

    def on_bpm_change(self, value):
        self.bpm = int(value)
        self.interval = 60.0 / self.bpm
        self.update_bpm_label()

    def change_bpm(self, delta):
        new_bpm = max(30, min(240, self.bpm + delta))
        self.bpm_slider.set(new_bpm)
        self.on_bpm_change(new_bpm)

    def update_bpm_label(self):
        self.bpm_label.config(text=f"{self.bpm} BPM")

    def flash_indicator(self, accent=False):
        color = "#f1c40f" if accent else "#2ecc71"
        self.indicator.itemconfig(self.circle, fill=color)
        self.root.after(100, lambda: self.indicator.itemconfig(self.circle, fill="#34495e"))

    def play_sound(self, accent=False):
        if SOUND_AVAILABLE:
            frequency = 800 if accent else 600
            duration = 80 if accent else 50
            winsound.Beep(frequency, duration)
        else:
            # Альтернатива — вывод в консоль
            print("🔔" if accent else "•", end=" ", flush=True)

    def metronome_loop(self):
        self.beat_count = 0
        while self.running:
            start_time = time.time()
            # Определяем, акцентная ли доля
            accent = self.accent_var.get() and (self.beat_count % 4 == 0)
            # Визуализация и звук
            self.root.after(0, lambda a=accent: self.flash_indicator(a))
            self.root.after(0, lambda a=accent: self.play_sound(a))
            self.beat_count += 1
            # Ожидание до следующего удара
            elapsed = time.time() - start_time
            sleep_time = max(0, self.interval - elapsed)
            time.sleep(sleep_time)

    def start_stop(self):
        if self.running:
            self.stop()
        else:
            self.start()

    def start(self):
        if self.running:
            return
        self.running = True
        self.start_btn.config(text="⏸ Пауза", bg="#f39c12")
        self.stop_btn.config(state=tk.NORMAL)
        self.status_label.config(text="Идёт...")
        self.thread = threading.Thread(target=self.metronome_loop, daemon=True)
        self.thread.start()

    def stop(self):
        self.running = False
        self.start_btn.config(text="▶ Старт", bg="#2ecc71")
        self.stop_btn.config(state=tk.DISABLED)
        self.status_label.config(text="Остановлено")
        self.indicator.itemconfig(self.circle, fill="#34495e")
        if self.thread and self.thread.is_alive():
            self.thread.join(timeout=0.1)

if __name__ == "__main__":
    root = tk.Tk()
    app = StepMetronome(root)
    root.mainloop()
