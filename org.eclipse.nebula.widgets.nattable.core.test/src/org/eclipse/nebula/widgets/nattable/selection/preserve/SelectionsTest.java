/*******************************************************************************
 * Copyright (c) 2014 Jonas Hugo, Markus Wahl.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jonas Hugo <Jonas.Hugo@jeppesen.com>,
 *       Markus Wahl <Markus.Wahl@jeppesen.com> - initial test
 *     Dirk Fauth <dirk.fauth@googlemail.com> - made Selections.Row a static inner class
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.selection.preserve;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashSet;

import org.eclipse.nebula.widgets.nattable.selection.preserve.Selections.CellPosition;
import org.junit.Test;

public class SelectionsTest {

    private Selections<String[]> testee = new Selections<String[]>();

    private Serializable rowA = "rowA";

    private Serializable rowB = "rowB";

    /**
     * Each row consist of an array of String, one String for each column, i e
     * each cell is a String.
     */
    private String[] rowObjectA = new String[] { "good", "day" };

    private String[] rowObjectB = new String[] { "bad", "night" };

    private int columnPosition1 = 1;

    private int columnPosition2 = 2;

    private int columnPosition3 = 3;

    @Test
    public void Never_Selected_Cell_Is_Not_Selected() {
        assertFalse(this.testee.isSelected(this.rowA, this.columnPosition2));
    }

    @Test
    public void Selecting_A_Cell_For_Unselected_Row() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        assertTrue(this.testee.isSelected(this.rowA, this.columnPosition2));
    }

    @Test
    public void Selecting_A_Cell_For_Already_Selected_Row() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition1);
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        assertTrue(this.testee.isSelected(this.rowA, this.columnPosition1));
        assertTrue(this.testee.isSelected(this.rowA, this.columnPosition2));
    }

    @Test
    public void Clear_Removes_All_Selections() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition1);
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        this.testee.select(this.rowB, this.rowObjectB, this.columnPosition2);
        this.testee.clear();
        assertFalse(this.testee.isSelected(this.rowA, this.columnPosition1));
        assertFalse(this.testee.isSelected(this.rowA, this.columnPosition2));
        assertFalse(this.testee.isSelected(this.rowB, this.columnPosition2));

        assertTrue(this.testee.getRows().isEmpty());
        assertTrue(this.testee.getColumnPositions().isEmpty());
    }

    @Test
    public void Deselecting_Cells_Does_Only_Affect_Those_Cells() {
        // cell not to be touched
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition1);

        // Cells to be touched
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        this.testee.select(this.rowB, this.rowObjectB, this.columnPosition2);
        this.testee.deselect(this.rowA, this.columnPosition2);
        this.testee.deselect(this.rowB, this.columnPosition2);

        assertTrue(this.testee.isSelected(this.rowA, this.columnPosition1));
        assertFalse(this.testee.isSelected(this.rowA, this.columnPosition2));
        assertFalse(this.testee.isSelected(this.rowB, this.columnPosition2));
    }

    @Test
    public void Fully_Deselected_Row_Doesent_Linger() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition1);
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        this.testee.deselect(this.rowA, this.columnPosition1);
        this.testee.deselect(this.rowA, this.columnPosition2);

        assertFalse(this.testee.isRowSelected(this.rowA));
    }

    @Test
    public void None_Selected_Cells_Is_Empty() {
        assertTrue(this.testee.isEmpty());
    }

    @Test
    public void Selected_Cell_Is_Not_Empty() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition1);
        assertFalse(this.testee.isEmpty());
    }

    @Test
    public void Fully_Deselecting_All_Rows_Causes_Is_Empty() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition1);
        this.testee.deselect(this.rowA, this.columnPosition1);
        assertTrue(this.testee.isEmpty());
    }

    @Test
    public void getSelections_Retrieves_All_Cells() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        this.testee.select(this.rowB, this.rowObjectB, this.columnPosition2);

        HashSet<CellPosition<String[]>> actualCells = new HashSet<CellPosition<String[]>>(
                this.testee.getSelections());

        HashSet<CellPosition<String[]>> expectedCells = new HashSet<CellPosition<String[]>>();
        expectedCells.add(new CellPosition<String[]>(this.rowObjectA,
                this.columnPosition2));
        expectedCells.add(new CellPosition<String[]>(this.rowObjectB,
                this.columnPosition2));

        assertEquals(expectedCells, actualCells);
    }

    @Test
    public void getRows() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        this.testee.select(this.rowB, this.rowObjectB, this.columnPosition2);

        HashSet<Serializable> actualRowIds = new HashSet<Serializable>();
        for (Selections.Row<String[]> row : this.testee.getRows()) {
            actualRowIds.add(row.getId());
        }

        HashSet<Serializable> expectedRowIds = new HashSet<Serializable>();
        expectedRowIds.add(this.rowA);
        expectedRowIds.add(this.rowB);

        assertEquals(expectedRowIds, actualRowIds);
    }

    @Test
    public void getColumns() {
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition2);
        this.testee.select(this.rowA, this.rowObjectA, this.columnPosition3);
        this.testee.select(this.rowB, this.rowObjectB, this.columnPosition1);

        HashSet<Integer> actualColumns = new HashSet<Integer>(
                this.testee.getColumnPositions());

        HashSet<Integer> expectedColumns = new HashSet<Integer>();
        expectedColumns.add(this.columnPosition2);
        expectedColumns.add(this.columnPosition1);
        expectedColumns.add(this.columnPosition3);

        assertEquals(expectedColumns, actualColumns);
    }

}
