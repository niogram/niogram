/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.tool;

import java.util.List;

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.BitSetLLString;
import net.ognyanov.niogram.util.IntLLString;
import net.ognyanov.niogram.util.IntLLStringSet;

class FirstFollowComparator
{
    private boolean doFirst  = true;
    private boolean doFollow = true;

    public void compare(Grammar grammar)
    {
        if (grammar == null) {
            throw new IllegalArgumentException();
        }
        List<NonterminalRule> nonterminalRules =
            grammar.getNonterminalRules();
        if (nonterminalRules.size() == 0) {
            return;
        }
        new FFKComparator().compare(grammar);
        new FFKLComparator().compare(grammar);
        new FFKKLComparator().compare(grammar);
    }

    private class FFKComparator
    {
        public void compare(Grammar grammar)
        {
            if (!(grammar.hasFF() && grammar.hasFFK())) {
                return;
            }
            boolean gotF = false;
            boolean gotFK = false;
            boolean gotFL = false;
            boolean gotFLK = false;
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                gotF |= !rule.getFirst().isEmpty();
                gotFK |= !rule.getFirstK().isEmpty();
                gotFL |= !rule.getFollow().isEmpty();
                gotFLK |= !rule.getFollowK().isEmpty();
            }
            if (gotF && gotFK && doFirst) {
                boolean firstOK = true;
                System.out.println("============================");
                System.out.println("Comparing First  and FirstK:");
                System.out.println("============================");
                for (NonterminalRule rule : grammar.getNonterminalRules()) {
                    if (!compare(rule.getFirst(), rule.getFirstK())) {
                        System.out.println(rule.getDisplayName() + " : "
                                + rule.getFirst().toString()
                                + " - " + rule.getFirstK());
                        firstOK = false;
                    }
                }
                if (firstOK) {
                    System.out.println("OK");
                }
                else {
                    System.out.println("FAILED");
                }
                System.out.println("============================");
            }
            if (gotFL && gotFLK && doFollow) {
                boolean followOK = true;
                System.out.println("==============================");
                System.out.println("Comparing Follow  and FollowK:");
                System.out.println("==============================");
                for (NonterminalRule rule : grammar.getNonterminalRules()) {
                    if (!compare(rule.getFollow(), rule.getFollowK())) {
                        System.out.println(rule.getDisplayName() + " : "
                                + rule.getFollow().toString()
                                + " - " + rule.getFollowK());
                        followOK = false;
                    }
                }
                if (followOK) {
                    System.out.println("OK");
                }
                else {
                    System.out.println("FAILED");
                }
                System.out.println("============================");
            }
        }

        private boolean compare(BiasedBitSet bitSet, IntLLStringSet llStringSet)
        {
            if (bitSet.isEmpty() && llStringSet.isEmpty()) {
                return true;
            }
            boolean result = true;
            int current = bitSet.getStart();
            int end = bitSet.getNone();
            while ((current = bitSet.nextSetBit(current)) != end) {
                boolean keyOK = false;
                for (IntLLString llString : llStringSet) {
                    if (llString.length() > 0) {
                        int stringStart = llString.get(0);
                        if (bitSet.get(stringStart)) {
                            keyOK = true;
                            break;
                        }
                    }
                }
                if (!keyOK) {
                    result = false;
                    break;
                }
                current++;
            }
            for (IntLLString llString : llStringSet) {
                if (llString.isEmpty()) {
                    continue;
                }
                boolean llStringOK = false;
                if (llString.length() > 0) {
                    if (bitSet.get(llString.get(0))) {
                        llStringOK = true;
                    }
                }
                if (!llStringOK) {
                    result = false;
                    break;
                }
            }
            return result;
        }

    }

    private class FFKLComparator
    {
        public void compare(Grammar grammar)
        {
            if (!(grammar.hasFF() && grammar.hasFFKL())) {
                return;
            }
            boolean gotF = false;
            boolean gotFKL = false;
            boolean gotFL = false;
            boolean gotFLKL = false;
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                gotF |= !rule.getFirst().isEmpty();
                gotFKL |= !rule.getFirstKL().isEmpty();
                gotFL |= !rule.getFollow().isEmpty();
                gotFLKL |= !rule.getFollowKL().isEmpty();
            }
            if (gotF && gotFKL && doFirst) {
                boolean firstOK = true;
                System.out.println("=============================");
                System.out.println("Comparing First  and FirstKL:");
                System.out.println("=============================");
                for (NonterminalRule rule : grammar.getNonterminalRules()) {
                    if (!compare(rule.getFirst(), rule.getFirstKL())) {
                        System.out.println(rule.getDisplayName() + " : "
                                + rule.getFirst()
                                + " - " + rule.getFirstKL());
                        firstOK = false;
                    }
                }
                if (firstOK) {
                    System.out.println("OK");
                }
                else {
                    System.out.println("FAILED");
                }
                System.out.println("============================");
            }
            if (gotFL && gotFLKL && doFollow) {
                boolean followOK = true;
                System.out.println("===============================");
                System.out.println("Comparing Follow  and FollowKL:");
                System.out.println("===============================");
                for (NonterminalRule rule : grammar.getNonterminalRules()) {
                    if (!compare(rule.getFollow(), rule.getFollowKL())) {
                        System.out.println(rule.getDisplayName() + " : "
                                + rule.getFollow()
                                + " - " + rule.getFollowKL());
                        followOK = false;
                    }
                }
                if (followOK) {
                    System.out.println("OK");
                }
                else {
                    System.out.println("FAILED");
                }
                System.out.println("============================");
            }
        }

        private boolean compare(BiasedBitSet bitSet,
                                BitSetLLString bitSetLLString)
        {
            if (bitSetLLString.length() == 0) {
                return bitSet.isEmpty();
            }
            return bitSet.equals(bitSetLLString.get(0));
        }

    }

    private class FFKKLComparator
    {
        public void compare(Grammar grammar)
        {
            if (!(grammar.hasFFK() && grammar.hasFFKL())) {
                return;
            }
            boolean gotFK = false;
            boolean gotFKL = false;
            boolean gotFLK = false;
            boolean gotFLKL = false;
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                gotFK |= !rule.getFirstK().isEmpty();
                gotFKL |= !rule.getFirstKL().isEmpty();
                gotFLK |= !rule.getFollowK().isEmpty();
                gotFLKL |= !rule.getFollowKL().isEmpty();
            }
            if (gotFK & gotFKL && doFirst) {
                boolean firstOK = true;
                System.out.println("=============================");
                System.out.println("Comparing FirstK and FirstKL:");
                System.out.println("=============================");
                for (NonterminalRule rule : grammar.getNonterminalRules()) {
                    if (!compare(rule.getFirstK(), rule.getFirstKL())) {
                        System.out
                            .println(
                                rule.getDisplayName() + " : " + rule.getFirstK()
                                        + " - " + rule.getFirstKL());
                        firstOK = false;
                    }
                }
                if (firstOK) {
                    System.out.println("OK");
                }
                else {
                    System.out.println("FAILED");
                }
                System.out.println("============================");
            }
            if (gotFLK && gotFLKL && doFollow) {
                boolean followOK = true;
                System.out.println("===============================");
                System.out.println("Comparing FollowK and FollowKL:");
                System.out.println("===============================");
                for (NonterminalRule rule : grammar.getNonterminalRules()) {
                    if (!compare(rule.getFollowK(), rule.getFollowKL())) {
                        System.out
                            .println(rule.getDisplayName() + " : "
                                    + rule.getFollowK()
                                    + " - " + rule.getFollowKL());
                        followOK = false;
                    }
                }
                if (followOK) {
                    System.out.println("OK");
                }
                else {
                    System.out.println("FAILED");
                }
                System.out.println("============================");
            }
        }

        private boolean compare(IntLLStringSet llStringSet,
                                BitSetLLString bitSetLLString)
        {
            if (llStringSet.isEmpty() && bitSetLLString.isEmpty()) {
                return true;
            }
            if (llStringSet.containsEmpty() != bitSetLLString.containsEmpty()) {
                return false;
            }

            boolean result = true;
            // for every LLString in LLStringSet
            // every key at position x is contained in
            // bitSetLLString[x]
            for (IntLLString llString : llStringSet) {
                if (llString.length() > bitSetLLString.length()) {
                    result = false;
                    break;
                }
                boolean llStringOK = true;
                if (llString.length() == 0) {
                    llStringOK =
                        bitSetLLString.containsEmpty();
                }
                else {
                    for (int i = 0; i < llString.length(); i++) {
                        if (!bitSetLLString.get(i).get(llString.get(i))) {
                            llStringOK = false;
                            break;
                        }
                    }
                }
                if (!llStringOK) {
                    result = false;
                    break;
                }
            }
            // for every key x in LLString[y] some LLString
            // in llStringSet contains x at position y
            outer: for (int i = 0; i < bitSetLLString.length(); i++) {
                BiasedBitSet bitSet = bitSetLLString.get(i);
                int key = bitSet.getStart();
                int end = bitSet.getNone();

                while ((key = bitSet.nextSetBit(key)) != end) {
                    boolean keyOK = false;
                    for (IntLLString llString : llStringSet) {
                        if (llString.length() > i
                                && llString.get(i) == key) {
                            keyOK = true;
                            break;
                        }
                    }
                    if (!keyOK) {
                        result = false;
                        break outer;
                    }
                    ++key;
                }
            }
            return result;
        }
    }
}
