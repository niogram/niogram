/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import java.io.Serializable;
import java.util.List;

import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.BitSetLLString;
import net.ognyanov.niogram.util.IntLLStringSet;

/**
 * A common interface for NonterminalRule and Block.
 *
 * @author Nikolay Ognyanov
 */
public interface Multiplex
{
    /**
     * Retrieves the list of alternatives.
     * 
     * @return the list of alternatives
     */
    public List<Alternative> getAlternatives();

    /**
     * Retrieves First/First conflicts.
     * 
     * @return the list of alternatives
     */
    public List<Conflict> getConflicts();

    /**
    * Retrieves the list of FirstK/FirstK conflicts.
    * 
    * @return the list of alternatives
    */
    public List<ConflictK> getConflictsK();

    /**
     * Retrieves the list of FirstKL/FirstKL conflicts.
     * 
     * @return the list of alternatives
     */
    public List<ConflictKL> getConflictsKL();

    /**
     * Retrieves the Firsst/Follow conflict.
     * 
     * @return the conflict
     */
    public BiasedBitSet getFfConflict();

    /**
     * Retrieves the FirsstK/FollowK conflict.
     * 
     * @return the conflict
     */
    public IntLLStringSet getFfConflictK();

    /**
    * Retrieves the FirsstKL/Follow KLconflict.
    * 
    * @return the conflict
    */
    public BitSetLLString getFfConflictKL();

    /**
     * Retrieves the minimum amount of lookahead
     * needed to resolve the FirstK/FirstK conflicts.
     * 
     * @return the minimum amount of lookahead
     */
    public int getMinK();

    /**
     * Retrieves the minimum amount of lookahead
     * needed to resolve the FirstKL/FirstKL conflicts.
     * 
     * @return the minimum amount of lookahead
     */
    public int getMinKL();

    /**
     * Retrieves the minimum amount of lookahead
     * needed to resolve the FirstK/FollowK conflicts.
     * 
     * @return the minimum amount of lookahead
     */
    public int getMinFfK();

    /**
     * Retrieves the minimum amount of lookahead
     * needed to resolve the FirstKL/FollowKL conflicts.
     * 
     * @return the minimum amount of lookahead
     */
    public int getMinFfKL();

    /**
     * Retrieves the nullability status of the object.
     * 
     * @return true if the object is nullable; otherwise false
     */
    public boolean isNullable();

    public static class Conflict
        implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private Alternative       source           = null;
        private Alternative       target           = null;
        private BiasedBitSet      conflict         = null;

        public Conflict(Alternative source, Alternative target,
                        BiasedBitSet conflict)
        {
            this.source = source;
            this.target = target;
            this.conflict = conflict;
        }

        public Alternative getSource()
        {
            return source;
        }

        public Alternative getTarget()
        {
            return target;
        }

        public BiasedBitSet getConflictSet()
        {
            return conflict;
        }

        @Override
        public String toString()
        {
            return "Conflict     ["
                    + "source=" + source.getDisplayName()
                    + ", target=" + target.getDisplayName()
                    + ", conflict=" + conflict
                    + "]";
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result =
                prime * result + ((conflict == null) ? 0 : conflict.hashCode());
            result =
                prime * result + ((source == null) ? 0 : source.hashCode());
            result =
                prime * result + ((target == null) ? 0 : target.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Conflict other = (Conflict) obj;
            if (conflict == null) {
                if (other.conflict != null)
                    return false;
            }
            else if (!conflict.equals(other.conflict))
                return false;
            if (source == null) {
                if (other.source != null)
                    return false;
            }
            else if (!source.equals(other.source))
                return false;
            if (target == null) {
                if (other.target != null)
                    return false;
            }
            else if (!target.equals(other.target))
                return false;
            return true;
        }
    }

    public static class ConflictK
        implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private Alternative       source;
        private Alternative       target;
        private IntLLStringSet    conflict;

        public ConflictK(Alternative source, Alternative target,
                         IntLLStringSet conflict)
        {
            this.source = source;
            this.target = target;
            this.conflict = conflict;
        }

        public Alternative getSource()
        {
            return source;
        }

        public Alternative getTarget()
        {
            return target;
        }

        public IntLLStringSet getConflictSet()
        {
            return conflict;
        }

        @Override
        public String toString()
        {
            return "ConflictK    ["
                    + "source=" + source.getDisplayName()
                    + ", target=" + target.getDisplayName()
                    + ", conflict=" + conflict
                    + "]";
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result =
                prime * result + ((conflict == null) ? 0 : conflict.hashCode());
            result =
                prime * result + ((source == null) ? 0 : source.hashCode());
            result =
                prime * result + ((target == null) ? 0 : target.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConflictK other = (ConflictK) obj;
            if (conflict == null) {
                if (other.conflict != null)
                    return false;
            }
            else if (!conflict.equals(other.conflict))
                return false;
            if (source == null) {
                if (other.source != null)
                    return false;
            }
            else if (!source.equals(other.source))
                return false;
            if (target == null) {
                if (other.target != null)
                    return false;
            }
            else if (!target.equals(other.target))
                return false;
            return true;
        }
    }

    public static class ConflictKL
        implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private Alternative       source;
        private Alternative       target;
        private BitSetLLString    conflict;

        public ConflictKL(Alternative source, Alternative target,
                          BitSetLLString conflict)
        {
            this.source = source;
            this.target = target;
            this.conflict = conflict;
        }

        public Alternative getSource()
        {
            return source;
        }

        public Alternative getTarget()
        {
            return target;
        }

        public BitSetLLString getConflictSet()
        {
            return conflict;
        }

        @Override
        public String toString()
        {
            return "ConflictKL   ["
                    + "source=" + source.getDisplayName()
                    + ", target=" + target.getDisplayName()
                    + ", conflict=" + conflict
                    + "]";
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result =
                prime * result + ((conflict == null) ? 0 : conflict.hashCode());
            result =
                prime * result + ((source == null) ? 0 : source.hashCode());
            result =
                prime * result + ((target == null) ? 0 : target.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConflictKL other = (ConflictKL) obj;
            if (conflict == null) {
                if (other.conflict != null)
                    return false;
            }
            else if (!conflict.equals(other.conflict))
                return false;
            if (source == null) {
                if (other.source != null)
                    return false;
            }
            else if (!source.equals(other.source))
                return false;
            if (target == null) {
                if (other.target != null)
                    return false;
            }
            else if (!target.equals(other.target))
                return false;
            return true;
        }
    }
}
