package com.projectmanagementsystembackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLinkResponse {

    private String payment_link_url;
    private String getPayment_link_id;
}
