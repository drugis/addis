package org.drugis.addis.presentation;

@SuppressWarnings("serial")
public class SelectableStudyCharTableModel extends StudyCharTableModel {

	public SelectableStudyCharTableModel(SelectableStudyListPresentationModel pm, PresentationModelFactory pmf) {
		super(pm, pmf);
	}
	
	@Override
	public int getColumnCount() {
		return super.getColumnCount() + 1;
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		if (getRowCount() < 1) {
			return Object.class;
		}
		Object value = getValueAt(0, c);
		return (value == null ? Object.class : value.getClass());
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < 0 || columnIndex >= getColumnCount()) {
			throw new IndexOutOfBoundsException("column index (" + columnIndex + ") out of bounds");
		}
		if (rowIndex < 0 || rowIndex >= getRowCount()) {
			throw new IndexOutOfBoundsException("row index (" + rowIndex + ") out of bounds");
		}
		
		if (columnIndex == 0) {
			return getVisibleModelByRow(rowIndex).getValue();
		}	
		
		return super.getValueAt(rowIndex, columnIndex - 1);
	}

	private TypedHolder<Boolean> getVisibleModelByRow(int rowIndex) {
		return ((SelectableStudyListPresentationModel)d_pm).getSelectedStudyBooleanModel(d_pm.getIncludedStudies().getValue().get(rowIndex));
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "";
		}
		
		return super.getColumnName(columnIndex - 1);
	}		
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}
	
	@Override
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			getVisibleModelByRow(rowIndex).setValue((Boolean) newValue);
		}
	}
}
