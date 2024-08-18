![CarSoftBannerSmall](https://github.com/user-attachments/assets/78d86992-12b1-4e0d-974a-707aba228ce2)
# Контроллер и android-приложение для выполнения закрытых поворотов и движения задним ходом на автомобиле.
Просмотр изображения широкоугольной передней камеры в закрытом повороте;
Освещение поворота противотуманными фарами;
Просмотр изображения широкоугольной задней камерой при движении задним ходом;
Включение аварийных огней при движении задним ходом, если не включен поворотник;
Использование только существующих органов управления автомобилем;
Настройка параметров работы и управление режимами тестирования посредством android-приложения.

# Стадия и задачи
Проект прошел PoC-стадию на базе действующего прототипа,
текущий цикл:
- написание читаемого кода контроллера;
- написание удобного android-приложения для настройки;
- соединение контроллера и смартфона с использованием Bluetooth SPP или GATT профилей в зависимсость от результатов работы Bluetooth модуля (Blutooth Low Energy в приоритете).

# [Wiki проекта](https://github.com/falconArdente/Car-controller_android-application/wiki)

# Аппаратная платформа, среда разработки, как собрать

## Контроллер
- ATmega328P (Распиновка под Arduino Pro Mini или Arduino Pro);
- Среда разработки Arduino 1.8.

## Bluetooth - модуль

- DX-BT18

## Android-приложение
- Операционная система Android 8.1 и выше;
- Среда разработки Android Studio Jellyfish | 2023.3.1 Patch 1;

## Компиляция и загрузка в микроконтроллер
Файлы из папки [videoCamModule](https://github.com/falconArdente/Car-controller_android-application/tree/bd982d7455a36e3f3930be44132e2ab3c56b03bf/VideoCamModule) 
необходимо расположить в одной папке, файл VideoCamModule.ino открыть с помощью среды разработки Arduino 1.8 (или выше) и загрузить в микроконтроллер встроенными средствами среды.
