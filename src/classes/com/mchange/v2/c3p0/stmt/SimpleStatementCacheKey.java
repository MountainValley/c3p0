/*
 * Distributed as part of c3p0 v.0.9.0-pre2
 *
 * Copyright (C) 2005 Machinery For Change, Inc.
 *
 * Author: Steve Waldman <swaldman@mchange.com>
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2.1, as 
 * published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; see the file LICENSE.  If not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */


package com.mchange.v2.c3p0.stmt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.lang.reflect.Method;

final class SimpleStatementCacheKey extends StatementCacheKey
{
    static StatementCacheKey _find( Connection pcon, Method stmtProducingMethod, Object[] args )
    {
	///BEGIN FIND LOGIC///
	String stmtText = (String) args[0];
	boolean is_callable = stmtProducingMethod.getName().equals("prepareCall");
	int result_set_type;
	int result_set_concurrency;

	int[] columnIndexes;
	String[] columnNames;
	Integer autogeneratedKeys;
	Integer resultSetHoldability;

	if (args.length == 1)
	    {
		result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
		result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
		columnIndexes          = null;
		columnNames            = null;
		autogeneratedKeys      = null;
		resultSetHoldability   = null;
	    }
	else if (args.length == 2)
	    {
		Class[] argTypes = stmtProducingMethod.getParameterTypes();
		if (argTypes[1].isArray())
		    {
			Class baseType = argTypes[1].getComponentType();
			if (baseType == int.class) //second arg is columnIndexes
			    {
				result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
				result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
				columnIndexes          = (int[]) args[1];
				columnNames            = null;
				autogeneratedKeys      = null;
				resultSetHoldability   = null;
			    }
			else if (baseType == String.class)
			    {
				result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
				result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
				columnIndexes          = null;
				columnNames            = (String[]) args[1];
				autogeneratedKeys      = null;
				resultSetHoldability   = null;
			    }
			else
			    throw new IllegalArgumentException("c3p0 probably needs to be updated for some new " +
							       "JDBC spec! As of JDBC3, we expect two arg statement " +
							       "producing methods where the second arg is either " +
							       "an int, int array, or String array.");
		    }
		else //it should be a boxed int, autogeneratedKeys
		    {
			result_set_type        = ResultSet.TYPE_FORWARD_ONLY;
			result_set_concurrency = ResultSet.CONCUR_READ_ONLY;
			columnIndexes          = null;
			columnNames            = null;
			autogeneratedKeys      = (Integer) args[1];
			resultSetHoldability   = null;
		    }
	    }
	else if (args.length == 3)
	    {
		result_set_type        = ((Integer) args[1]).intValue();
		result_set_concurrency = ((Integer) args[2]).intValue();
		columnIndexes          = null;
		columnNames            = null;
		autogeneratedKeys      = null;
		resultSetHoldability   = null;
	    }
	else if (args.length == 4)
	    {
		result_set_type        = ((Integer) args[1]).intValue();
		result_set_concurrency = ((Integer) args[2]).intValue();
		columnIndexes          = null;
		columnNames            = null;
		autogeneratedKeys      = null;
		resultSetHoldability   = (Integer) args[3];
	    }
	else
	    throw new IllegalArgumentException("Unexpected number of args to " + 
					       stmtProducingMethod.getName() );
	///END FIND LOGIC///


	return new SimpleStatementCacheKey( pcon, 
					    stmtText, 
					    is_callable, 
					    result_set_type, 
					    result_set_concurrency,
					    columnIndexes,
					    columnNames,
					    autogeneratedKeys,
					    resultSetHoldability );
    }

    SimpleStatementCacheKey( Connection physicalConnection,
			     String stmtText,
			     boolean is_callable,
			     int result_set_type,
			     int result_set_concurrency,
			     int[] columnIndexes,
			     String[] columnNames,
			     Integer autogeneratedKeys,
			     Integer resultSetHoldability )
    {
	super( physicalConnection,
	       stmtText,
	       is_callable,
	       result_set_type,
	       result_set_concurrency,
	       columnIndexes,
	       columnNames,
	       autogeneratedKeys,
	       resultSetHoldability );
    }

    public boolean equals( Object o )
    { return StatementCacheKey.equals( this, o ); }

    public int hashCode()
    { return StatementCacheKey.hashCode( this ); }
}
