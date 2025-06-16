package com.marche.marche.modele;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String recipientToken;
    private String title;
    private String body;
    private Map<String, String> data;
}
