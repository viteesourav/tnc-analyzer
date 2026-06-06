package com.tnc.entity;

/*
    This is an Enum Class -> Where we provide pre-defined options that can be while designing DTO structure.
    Enum ensures consistency !
    Here:
        AnalysisSoruce --> This specifies the Source of Term & Conditions from the below option
*/
public enum AnalysisSource {
    TEXT,
    PDF,
    IMAGE,
    CHROME_EXTENSION
}
