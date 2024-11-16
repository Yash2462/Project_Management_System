package com.projectmanagementsystembackend.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    private Long senderId;
    private Long projectId;
    private String content;
}
