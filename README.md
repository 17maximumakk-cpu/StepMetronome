StepMetronome — Степ-метроном на 7 языках
StepMetronome — коллекция из семи независимых реализаций метронома с визуальной индикацией ритма. Каждая версия работает на своём языке программирования и предлагает гибкие настройки для музыкантов, танцоров и всех, кто хочет развивать чувство ритма.

✨ Общие возможности
🎵 Регулировка темпа (BPM) от 30 до 240 ударов в минуту

🟢 Визуальная индикация такта (мигающий индикатор/круг)

🔊 Звуковой сигнал (щёлчок/бип) на каждый удар

🎯 Акцент на первой доле (сильный удар) и слабые удары

🎛️ Настройка громкости (опционально)

🔄 Отображение текущего темпа в реальном времени

🖱️ Управление: Старт/Стоп, ползунок BPM, кнопки +/- BPM

🌐 Интерфейсы:

Десктопные GUI: Python (Tkinter), Java (Swing), C# (WinForms)

Веб-приложения: JavaScript (HTML+CSS+JS), Go, Rust, PHP (сервер + клиент)

📋 Сравнение реализаций
Язык	Интерфейс	Звук	Визуализация	BPM диапазон	Акцент
Python	Tkinter GUI	winsound/beep	Мигающий круг	30-240	✅
JavaScript	Веб (Canvas)	Web Audio	Анимация	30-240	✅
Go	Веб (сервер)	Web Audio (клиент)	Анимация	30-240	✅
Rust	Веб (сервер)	Web Audio (клиент)	Анимация	30-240	✅
Java	Swing GUI	Toolkit.beep	Мигающий круг	30-240	✅
C#	WinForms GUI	Console.Beep	Мигающий круг	30-240	✅
PHP	Веб (сервер)	Web Audio (клиент)	Анимация	30-240	✅
🚀 Быстрый старт
Python
bash
# Tkinter встроен
python step_metronome.py
JavaScript (браузер)
Откройте step_metronome.html в браузере.

Go
bash
go run step_metronome.go
# Откройте http://localhost:8080
Rust
bash
cargo run
# Откройте http://localhost:8000
Java
bash
javac StepMetronome.java && java StepMetronome
C#
bash
csc /reference:System.Windows.Forms.dll /reference:System.Drawing.dll StepMetronome.cs
StepMetronome.exe
PHP
bash
php -S localhost:8000
# Откройте http://localhost:8000/step_metronome.php
