![CarSoftBannerSmall](https://github.com/user-attachments/assets/78d86992-12b1-4e0d-974a-707aba228ce2)
# Контроллер и android-приложение для выполнения закрытых поворотов и движения задним ходом на автомобиле.
- Просмотр изображения широкоугольной передней камеры в закрытом повороте;
- Освещение пространства поворота светом противотуманных фар;
- Управление цветом противотуманных фар комбинированием с подсветкой angel eye;
- Просмотр пространства широкоугольной задней камерой при движении задним ходом;
- Включение аварийных огней при движении задним ходом, если не включен поворотник;
- Использование только существующих органов управления автомобилем;
- Настройка параметров работы и управление режимами тестирования с помощью android-приложения.

# Стадия и задачи
Проект прошел стадию proof of concept (изготовлен действующего прототип, прошедший полугодовые испытания),
текущий цикл:
- проектировка печатной платы ✔;
- подготовка схемы смены состояний ✔;
- отладка новой версии контроллера ✔;
- написание читаемого кода контроллера ✔;
- подготовка описания пакетов обмена данными ✔;
- соединение контроллера и смартфона с использованием Bluetooth Low Energy ✔;
- написание удобного android-приложения для настройки ✔;
- внедрение ✔;
- изготовление видео и документации.

# [Wiki проекта](https://github.com/falconArdente/Car-controller_android-application/wiki) содержит дополнительную документацию

# Аппаратная платформа, среда разработки, как собрать

## Контроллер
- ATmega328P (Распиновка под Arduino Pro Mini или Arduino Pro, схема изготовления печатной платы представлена в разделе wiki);
- Среда разработки Arduino 1.8.

## Bluetooth - модуль
- DX-BT18

## Android-приложение
- Операционная система Android 8.1 и выше;
- Среда разработки Android Studio Jellyfish | 2023.3.1 Patch 1;

## Компиляция и загрузка в микроконтроллер
Файлы из папки [videoCamModule](https://github.com/falconArdente/Car-controller_android-application/tree/bd982d7455a36e3f3930be44132e2ab3c56b03bf/VideoCamModule) 
необходимо расположить в одной папке, файл VideoCamModule.ino открыть с помощью среды разработки Arduino 1.8 (или выше) и загрузить в микроконтроллер встроенными средствами среды.

![GitHub top language](https://img.shields.io/github/languages/top/falconArdente/Car-controller_android-application)
![GitHub Repo stars](https://img.shields.io/github/stars/falconArdente/Car-controller_android-application)
![GitHub issues](https://img.shields.io/github/issues/falconArdente/Car-controller_android-application)
