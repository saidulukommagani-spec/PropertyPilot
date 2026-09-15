package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentChecklistItemResponse {

    private String documentCode;

    private String documentName;

    private Boolean mandatory;

    private Boolean uploaded;

    private String documentStatus;

    private Boolean availableWithOwner;

    private Boolean canBeProcuredByPropertyPilot;
}