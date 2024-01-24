package com.luifrangm.labels.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelsResponse {
    private float confidence;
    private String labelName;
    private LabelsGeometry geometry;
}
