package com.miu.alumnimanagementportal.dtos;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionStatusDto implements Serializable {
    private boolean connected;
    private boolean canMessage;
    private FriendRequestStatus status;
    private Long requestId;
}

