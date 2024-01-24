package com.luifrangm.labels.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelsGeometry {
    private float width;
    private float  height;
    private float  left;
    private float  top;
}
