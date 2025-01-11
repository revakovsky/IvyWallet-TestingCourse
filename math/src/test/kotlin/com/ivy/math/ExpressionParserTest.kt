package com.ivy.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.parser.ParseResult
import com.ivy.parser.Parser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ExpressionParserTest {

    private lateinit var parser: Parser<TreeNode>

    @BeforeEach
    fun setUp() {
        parser = expressionParser()
    }

    @ParameterizedTest
    @CsvSource(
        "3+6/3-(-10), 15.0",               // Tests addition, division, and handling of negative numbers.
        "5+6, 11.0",                       // Simple addition to validate basic functionality.
        "5.0000000, 5.0",                  // Ensures handling of trailing zeros in decimal values.
        "100/(10*10), 1.0",                // Tests division with nested multiplication for operator precedence.
        "-5+10, 5.0",                      // Negative number addition
        "0/1, 0.0",                        // Division by a non-zero
        "0*10+5, 5.0",                     // Zero multiplication
        "1+2*3-4/2, 5.0",                  // Operator precedence
        "(2+3)*(4-1), 15.0",               // Parentheses for precedence
        "1000000*1000000, 1.0E12",         // Large numbers
        "1/3, 0.3333333333333333",         // Decimal precision
        "-(-5), 5.0",                      // Double negative
        "10-5-5, 0.0",                     // Subtraction chaining
        "1+1-1+1-1+1, 2.0",                // Chained operations
        "(1+(2*(3+(4*(5+6))))), 95.0",     // Deeply nested parentheses
        "1/0, Infinity",                   // Division by zero (if allowed)
        "0/0, NaN",                        // Undefined division by zero
        "-5*-5, 25.0",                     // Multiplication of two negatives
        "10-20, -10.0",                    // Subtracting a larger positive number from a smaller one.
        "-5*-5, 25.0",                     // Multiplication of two negative numbers resulting in a positive result.
        "-15/3, -5.0",                     // Division of a negative number by a positive number.
        "15/(-3), -5.0",                   // Division of a positive number by a negative number.
        "-10/(-2), 5.0",                   // Division of two negative numbers resulting in a positive result.
        "(-3+6)*-2, -6.0",                 // Handling of parentheses, addition, and multiplication with a negative result.
        "-(4+6), -10.0",                   // Negative applied to the result of a parenthetical expression.
        "0-5, -5.0",                       // Subtraction resulting in a negative value.
        "-0.5*4, -2.0"                     // Multiplication with a negative decimal number.
    )
    fun `Test Evaluating Expression`(expression: String, expected: Double) {
        val result: ParseResult<TreeNode> = parser(expression).first()
        val actualResult: Double = result.value.eval()

        assertThat(actualResult).isEqualTo(expected)
    }

}
