package com.tnc.config;

/*
    This is a classic java design pattern to create utility or constant class.
    To prevenet the class from instanciating, making the class as final + private constructor totally locks it down.
    -- final keyword:
            -> prevents the class from being extented (inherited) by any other class.
            -> If not declared final, anyone can accidently inherit it. --> final tells java -> this is strictly completed here, no child allowed.
    -- Private consturctor:
            -> Bydefault, if you dont add a consturctor, Java insert a public constructor by default.
            -> This means, anyone can do the below:
                    AnalysisServiceConstants constant = new AnalysisServiceConstants();
            -> Crating a object instance of a class where inside all variable are static -> is a waste of memory.
            -> by declaring private constructor ---> We explictly block use of "new" keyword.
*/
public final class AnalysisServiceConstants {
    
    // private constructor prevents instatiation of this utility class..
    private AnalysisServiceConstants() {}

    public static final String MODEL = "gemini-2.5-flash";   // Constant for the model used for Gemini API -  robust, free-tier supported.
    public static final int MAX_TEXT_LENGTH = 50_000;      // Constant for verifying the max text length in input
}
