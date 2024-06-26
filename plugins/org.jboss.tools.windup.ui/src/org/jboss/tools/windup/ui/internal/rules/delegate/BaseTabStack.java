/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.windup.ui.internal.rules.delegate;

import java.util.Map;

import jakarta.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jboss.tools.windup.ui.WindupUIPlugin;

import com.google.common.collect.Maps;

/**
 * Represents a stack of stabs.
 */
public class BaseTabStack {

	protected Map<CTabItem, TabWrapper> tabs = Maps.newHashMap();
	
	@Inject protected IEclipseContext context;
	@Inject @Optional protected FormToolkit toolkit;
	
	protected CTabFolder folder;
	
	protected void createFolder(Composite parent) {
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);
		ColorRegistry reg = JFaceResources.getColorRegistry();
		Color c1 = reg.get("org.eclipse.ui.workbench.ACTIVE_TAB_BG_START"), //$NON-NLS-1$
			  c2 = reg.get("org.eclipse.ui.workbench.ACTIVE_TAB_BG_END"); //$NON-NLS-1$
		folder = new CTabFolder(parent, SWT.NO_REDRAW_RESIZE | SWT.FLAT);
		folder.setSelectionBackground(new Color[] {c1, c2},	new int[] {100}, true);
		folder.setSelectionForeground(reg.get("org.eclipse.ui.workbench.ACTIVE_TAB_TEXT_COLOR")); //$NON-NLS-1$
		folder.setSimple(PlatformUI.getPreferenceStore().getBoolean(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS));
		folder.setBorderVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(folder);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		folder.setLayoutData(gd);
		folder.setFont(parent.getFont());
		folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem item = folder.getSelection();
				tabItemSelected(item);
			}
		});
	}
	
	protected void tabItemSelected(CTabItem item) {
	}
	
	protected <T> TabWrapper addTab(Class<T> clazz) {
		CTabItem item = new CTabItem(folder, SWT.BORDER);
		Composite parent = toolkit.createComposite(folder);
		GridLayoutFactory.fillDefaults().applyTo(parent);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);
		item.setControl(parent);
		IEclipseContext child = createTabContext(parent);
		child.set(CTabItem.class, item);
		T object = create(clazz, child);
		TabWrapper wrapper = new TabWrapper(object, child, item);
		tabs.put(item, wrapper);
		return wrapper;
	}
	
	protected IEclipseContext createTabContext(Composite parent) {
		IEclipseContext child = context.createChild();
		child.set(Composite.class, parent);
		child.set(FormToolkit.class, toolkit);
		return child;
	}
	
	protected <T> T create(Class<T> clazz, IEclipseContext context) {
		try {
			T result = ContextInjectionFactory.make(clazz, context);
			return result;
		}
		catch (Exception e) {
			WindupUIPlugin.logError("BaseTabStack::106 :: Error occurred while trying to create.", e);
			WindupUIPlugin.logErrorMessage("Class: " + clazz.getName());
			WindupUIPlugin.logErrorMessage("Message: " + e.getMessage());
		}
		return null;
	}
	
	public Control getControl() {
		return folder;
	}
	
	protected void closeTab() {
	}
	
	/**
	 * A wrapper for each child tab.
	 */
	public static class TabWrapper {
		
		private Object object;
		private IEclipseContext context;
		private CTabItem item;
		
		public TabWrapper(Object object, IEclipseContext context, CTabItem item) {
			this.context = context;
			this.object = object;
			this.item = item;
		}

		public Object getObject() {
			return object;
		}
		
		public IEclipseContext getContext() {
			return context;
		}
		
		public CTabItem getItem() {
			return item;
		}
	}
}
