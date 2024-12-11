package com.cej.nc.commons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Xdd {
    String sName;
    String pos;
    String sType;
    String pid;
    String star;
}
