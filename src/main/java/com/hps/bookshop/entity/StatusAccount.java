package com.hps.bookshop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusAccount {
    NOT_ACTIVATED(0), ACTIVATED(1), LOCKED(-1);

    private int status;
}
