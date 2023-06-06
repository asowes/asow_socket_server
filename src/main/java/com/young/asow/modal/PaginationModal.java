package com.young.asow.modal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationModal<T> {

    List<T> data;

    int page;

    int size;

    int total;
}
