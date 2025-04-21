package org.yrti.inventory.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
  public ProductNotFoundException(Long id) {
    super("Продукт с ID " + id + " не найден.");
  }

}

// TODO-Крит тебе надо в проект добавить статический анализатор кода + файл с код стайлом. Есть какой то гугловский, если мне не изменяет память.
//  Анализатор кода при сборке проекта сразу подсветит кривое форматирование, а файлик с код стайлом позволит форматировать код в нужный формат при помощи ctrl+alt+L