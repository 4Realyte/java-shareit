package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.dto.RequestItemShort;
import ru.practicum.shareit.user.dto.UserIdInfo;

public interface ItemInfo {
    Long getId();

    String getName();

    UserIdInfo getOwner();

    RequestItemShort getRequestItem();
}
