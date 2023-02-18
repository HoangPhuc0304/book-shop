package com.hps.bookshop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1);

    private int direction;
}
