package com.projectmanagementsystembackend.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteRequest {
    private String email;
    private Long projectId;
}
