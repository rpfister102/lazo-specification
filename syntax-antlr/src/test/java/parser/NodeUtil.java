package parser;

import bazolang.LazoParser;
import bazolang.LazoParser.VersionDirectiveContext;
import lexer.LexerUtil;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Assert;

import java.util.List;

public class NodeUtil {
    public static void assertVersion(VersionDirectiveContext versionNode, int major, int minor) {
        Assert.assertEquals(String.valueOf(major), versionNode.INTEGER(0).getText());
        Assert.assertEquals(String.valueOf(minor), versionNode.INTEGER(1).getText());
    }

    public static void assertContractDecl(LazoParser.ContractDeclarationContext contractNode, String... names) {
        for (int i = 0; i < contractNode.IDENTIFIER().size(); i++) {
            LexerUtil.assertIdentifier(contractNode.IDENTIFIER(i).getSymbol(), names[i]);
        }
    }

    public static void assertInterfaceDecl(LazoParser.InterfaceDeclarationContext interfaceNode, String name) {
        LexerUtil.assertIdentifier(interfaceNode.IDENTIFIER().getSymbol(), name);
    }

    public static void assertVariableDecl(LazoParser.VariableDeclarationContext variableNode,
                                          String name, String type, String assignment) {
        LexerUtil.assertIdentifier(variableNode.IDENTIFIER().getSymbol(), name);
        Assert.assertEquals(type, variableNode.type().getChild(0).getText());

        if (assignment != null) {
            Assert.assertEquals(assignment, variableNode.assignment().getChild(1).getText());
        } else {
            Assert.assertNull(variableNode.assignment());
        }
    }

//    public static void assertNewArrayCreation(LazoParser.ArrayCreationContext arrayCreationContext,
//                                                   String type, String expression, String... values) {
//
//        Assert.assertEquals(type, variableNode.type().getChild(0).getText());
//
//    }

//    public static void assertNewMapCreation(LazoParser.MapCreationContext mapCreationContext,
//                                              String type, String expression, String... values) {
//
//        Assert.assertEquals(type, variableNode.type().getChild(0).getText());
//
//    }

    public static void assertStructDecl(LazoParser.StructDeclarationContext structNode,
                                        String name, int totalFields) {
        LexerUtil.assertIdentifier(structNode.IDENTIFIER().getSymbol(), name);
        Assert.assertEquals(totalFields, structNode.structField().size());
    }

    public static void assertEventDecl(LazoParser.EventDeclarationContext eventNode,
                                       String name, int totalFields) {
        LexerUtil.assertIdentifier(eventNode.IDENTIFIER().getSymbol(), name);
        assertParameterListSize(eventNode.paramList(), totalFields);
    }

    public static void assertEnumDecl(LazoParser.EnumDeclarationContext enumNode, String name, String... values) {
        LexerUtil.assertIdentifier(enumNode.IDENTIFIER(0).getSymbol(), name);

        for (int i = 0; i < values.length; i++) {
            LexerUtil.assertIdentifier(enumNode.IDENTIFIER(i + 1).getSymbol(), values[i]);
        }
    }

    public static void assertConstructorDecl(LazoParser.ConstructorDeclarationContext constructorNode,
                                             int totalParams, int totalStatements, int totalAnnotations) {
        assertParameterListSize(constructorNode.paramList(), totalParams);
        Assert.assertEquals(totalStatements, constructorNode.statementBlock().statement().size());
        Assert.assertEquals(totalAnnotations, constructorNode.annotation().size());
    }

    public static void assertFunctionHead(LazoParser.FunctionHeadContext functionHeadNode,
                                          String name, int totalParams, String... returnTypes) {
        LexerUtil.assertIdentifier(functionHeadNode.IDENTIFIER().getSymbol(), name);
        assertParameterListSize(functionHeadNode.paramList(), totalParams);
    }

    public static void assertParameterListSize(LazoParser.ParamListContext paramsList, int expected) {
        if (expected == 0) {
            Assert.assertNull(paramsList);
        } else {
            Assert.assertEquals(expected, paramsList.parameter().size());
        }
    }

    public static void assertExpression(LazoParser.ExpressionContext expressionNode, String expected) {
        Assert.assertEquals(expected, expressionNode.getText());
    }

    public static void assertIndexAccessExpression(LazoParser.ExpressionContext indexAccessNode,
                                                   String expectedDesignator, String expectedExpression) {
        assertExpression(indexAccessNode.getChild(LazoParser.ExpressionContext.class, 0), expectedDesignator);
        assertExpression(indexAccessNode.getChild(LazoParser.ExpressionContext.class, 1), expectedExpression);
    }

    public static void assertMemberAccessExpression(LazoParser.ExpressionContext memberAccessNode,
                                                    String expectedParent, String expectedMemberField) {
        assertExpression(memberAccessNode.getChild(LazoParser.ExpressionContext.class, 0), expectedParent);
        LexerUtil.assertIdentifier(memberAccessNode.IDENTIFIER().getSymbol(), expectedMemberField);
    }

    public static void assertCallExpression(LazoParser.ExpressionContext callNode,
                                            String func, String... args) {
        assertExpression(callNode.getChild(LazoParser.ExpressionContext.class, 0), func);

        var argList = callNode.getChild(LazoParser.ArgumentListContext.class, 0);
        if (args.length == 0) {
            Assert.assertNull(argList);
        } else {
            for (int i = 0; i < args.length; i++) {
                assertExpression(argList.getChild(LazoParser.ExpressionContext.class, i), args[i]);
            }
        }
    }

    public static void assertTerminalNode(ParseTree tree, String expected) {
        Assert.assertEquals(expected, tree.getText());
    }

    public static void removeNewlines(List<? extends ParserRuleContext> nodes) {
        var it = nodes.iterator();
        while (it.hasNext()) {
            var item = it.next();
            if (item.getRuleIndex() == LazoParser.NLS) {
                it.remove();
            }
        }
    }
}
