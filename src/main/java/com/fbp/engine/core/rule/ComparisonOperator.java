package com.fbp.engine.core.rule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComparisonOperator {
    GT(">", (a, b) -> a > b),
    LT("<", (a, b) -> a < b),
    GTE(">=", (a, b) -> a >= b),
    LTE("<=", (a, b) -> a <= b),
    EQ("==", (a, b) -> Double.compare(a, b) == 0),
    NE("!=", (a, b) -> Double.compare(a, b) != 0);

    private final String symbol;
    private final NumberComparison comparison;

    public boolean compare(double a, double b) {
        return comparison.compare(a, b);
    }
}
