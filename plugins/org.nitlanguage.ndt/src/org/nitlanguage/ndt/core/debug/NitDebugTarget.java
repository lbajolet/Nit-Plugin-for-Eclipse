package org.nitlanguage.ndt.core.debug;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.nitlanguage.ndt.core.debug.events.DebugOverEvent;
import org.nitlanguage.ndt.core.debug.events.NitSuspendedEvent;
import org.nitlanguage.ndt.core.debug.network.NitDebuggerConnector;
import org.nitlanguage.ndt.core.debug.network.NitDebuggerReader;
import org.nitlanguage.ndt.core.debug.network.NitDebuggerWriter;
import org.nitlanguage.ndt.core.debug.network.NitMessageInterpreter;

public class NitDebugTarget extends Observable implements IDebugTarget,
		Observer {

	private IProcess process;
	private ILaunch launch;
	private NitDebuggerConnector target;
	private NitDebuggerReader reader;
	private NitDebuggerWriter writer;

	private NitMessageInterpreter messageInterpret;

	private NitThread[] threads;

	private NitStackFrame[] stackTrace;

	private boolean isStopped;

	private boolean readingInnerVar = false;

	public NitDebugTarget(ILaunch launch, IProcess process, String host,
			int port) throws UnknownHostException, IOException {
		this.target = new NitDebuggerConnector().connect(host, port);
		this.reader = new NitDebuggerReader(this.target);
		this.writer = new NitDebuggerWriter(this.target);
		this.threads = new NitThread[1];
		this.threads[0] = new NitThread(this);
		this.isStopped = true;
		this.stackTrace = new NitStackFrame[0];
		this.messageInterpret = new NitMessageInterpreter(this);
		this.messageInterpret.observe(this.reader);
		this.messageInterpret.addObserver(this);
		fireDebugEvent(new DebugEvent(this, DebugEvent.CREATE));
		fireDebugEvent(new DebugEvent(this, DebugEvent.RESUME));
		new Thread(this.reader).start();
		DebugPlugin.getDefault().getBreakpointManager()
				.addBreakpointListener(this);
		installDeferredBreakpoints();
	}

	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	private void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(NitDebugConstants.MODEL_IDENTIFIER);
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}

	public NitStackFrame[] getStackTrace() {
		return this.stackTrace;
	}

	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}

	@Override
	public ILaunch getLaunch() {
		return this.launch;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canTerminate() {
		return !isTerminated();
	}

	@Override
	public boolean isTerminated() {
		return !this.target.isConnected();
	}

	@Override
	public void terminate() throws DebugException {
		try {
			this.writer.abort();
			this.target.disconnect();
		} catch (IOException e) {
			try {
				this.process.terminate();
			} catch (DebugException f) {
				f.printStackTrace();
			}
			e.printStackTrace();
		}
		fireDebugEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}

	@Override
	public boolean canResume() {
		return this.isStopped && !this.isTerminated();
	}

	@Override
	public boolean canSuspend() {
		return !this.isStopped && !this.isTerminated();
	}

	@Override
	public boolean isSuspended() {
		return this.isStopped && !this.isTerminated();
	}

	@Override
	public void resume() throws DebugException {
		try {
			this.writer.continue_exec();
			fireDebugEvent(new DebugEvent(this.stackTrace[0],
					DebugEvent.RESUME, DebugEvent.CLIENT_REQUEST));
			this.isStopped = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void suspend() throws DebugException {
		try {
			this.writer.suspend_exec();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint)) {
			NitLineBreakpoint bp = (NitLineBreakpoint) breakpoint;
			IResource res = bp.getMarker().getResource();
			try {
				this.writer.place_breakpoint("test.nit", bp.getLineNumber());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {

	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	@Override
	public boolean canDisconnect() {
		return false;
	}

	@Override
	public void disconnect() throws DebugException {
	}

	@Override
	public boolean isDisconnected() {
		return false;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return null;
	}

	@Override
	public IProcess getProcess() {
		return this.process;
	}

	public void stepInto() throws DebugException, IOException {
		this.writer.stepInto();
		isStopped = false;
		fireDebugEvent(new DebugEvent(this.stackTrace[0], DebugEvent.STEP_INTO,
				DebugEvent.CLIENT_REQUEST));
	}

	public void stepOver() throws IOException {
		this.writer.stepOver();
		isStopped = false;
		fireDebugEvent(new DebugEvent(this.stackTrace[0], DebugEvent.STEP_OVER,
				DebugEvent.CLIENT_REQUEST));
	}

	public void stepOut() throws IOException {
		this.writer.stepOut();
		isStopped = false;
		fireDebugEvent(new DebugEvent(this.stackTrace[0],
				DebugEvent.STEP_RETURN, DebugEvent.CLIENT_REQUEST));
	}

	@Override
	public IThread[] getThreads() throws DebugException {
		return this.threads;
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return !isTerminated();
	}

	@Override
	public String getName() throws DebugException {
		return "Nit program";
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint instanceof NitLineBreakpoint) {
			return true;
		}
		return false;
	}

	private void fireDebugEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
	}

	public void getVariableWithName(String name) {
		try {
			this.writer.getVariable(name);
			this.readingInnerVar = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof NitSuspendedEvent) {
			try {
				this.writer.printStack();
				this.isStopped = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (arg instanceof NitStackFrame[]) {
			this.stackTrace = (NitStackFrame[]) arg;
			try {
				this.writer.printVariables();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (arg instanceof NitVariable[]) {
			if (!readingInnerVar) {
				this.stackTrace[0].setNitVars((NitVariable[]) arg);
				fireDebugEvent(new DebugEvent(this.stackTrace[0],
						DebugEvent.SUSPEND));
			} else {
				setChanged();
				notifyObservers(arg);
				this.readingInnerVar = false;
			}
		} else if (arg instanceof DebugOverEvent) {
			try {
				this.writer.debugOverAck();
				this.target.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					this.terminate();
				} catch (DebugException e1) {
					e1.printStackTrace();
				}
			}
			this.isStopped = true;
			fireDebugEvent(new DebugEvent(this, DebugEvent.TERMINATE));
		}
	}
}
