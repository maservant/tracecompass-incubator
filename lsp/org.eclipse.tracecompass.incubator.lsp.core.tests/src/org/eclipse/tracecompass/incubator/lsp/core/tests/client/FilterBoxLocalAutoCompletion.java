package org.eclipse.tracecompass.incubator.lsp.core.tests.client;

import static org.junit.Assert.assertEquals;

import org.eclipse.tracecompass.incubator.lsp.ui.lspFilterTextbox.FilterBoxLocalTextCompletion;
import org.junit.Test;

public class FilterBoxLocalAutoCompletion {

    /**
     * Tests quote completion
     */
   @Test
   public void completeQuoteTest() {

       //Double quote
       assertEquals("\"\"", FilterBoxLocalTextCompletion.autocomplete("\"", 1));

       assertEquals("t\"\"", FilterBoxLocalTextCompletion.autocomplete("t\"", 2));

       assertEquals("\"t", FilterBoxLocalTextCompletion.autocomplete("\"t", 1));

       assertEquals("\"\" ", FilterBoxLocalTextCompletion.autocomplete("\" ", 1));


       //Single quote
       assertEquals("''", FilterBoxLocalTextCompletion.autocomplete("'", 1));

       assertEquals("t''", FilterBoxLocalTextCompletion.autocomplete("t'", 2));

       assertEquals("'t", FilterBoxLocalTextCompletion.autocomplete("'t", 1));

       assertEquals("'' ", FilterBoxLocalTextCompletion.autocomplete("' ", 1));

   }

   /**
    * Test brackets completions
    */
   @Test
   public void completeBracket() {

       //Squared brackets
       assertEquals("[]", FilterBoxLocalTextCompletion.autocomplete("[", 1));

       assertEquals("t[]", FilterBoxLocalTextCompletion.autocomplete("t[", 2));

       assertEquals("[t", FilterBoxLocalTextCompletion.autocomplete("[t", 1));

       assertEquals("[] ", FilterBoxLocalTextCompletion.autocomplete("[ ", 1));

       assertEquals("[[]]", FilterBoxLocalTextCompletion.autocomplete("[[]", 2));

       //Normal brackets
       assertEquals("{}", FilterBoxLocalTextCompletion.autocomplete("{", 1));

       assertEquals("t{}", FilterBoxLocalTextCompletion.autocomplete("t{", 2));

       assertEquals("{t", FilterBoxLocalTextCompletion.autocomplete("{t", 1));

       assertEquals("{} ", FilterBoxLocalTextCompletion.autocomplete("{ ", 1));

       assertEquals("{{}}", FilterBoxLocalTextCompletion.autocomplete("{{}", 2));

   }

   /**
    * Tests parenthesis completion
    */
   @Test
   public void completeParenthesis() {

       assertEquals("{}", FilterBoxLocalTextCompletion.autocomplete("{", 1));

       assertEquals("t{}", FilterBoxLocalTextCompletion.autocomplete("t{", 2));

       assertEquals("{t", FilterBoxLocalTextCompletion.autocomplete("{t", 1));

       assertEquals("{} ", FilterBoxLocalTextCompletion.autocomplete("{ ", 1));

       assertEquals("{{}}", FilterBoxLocalTextCompletion.autocomplete("{{}", 2));

   }
}
