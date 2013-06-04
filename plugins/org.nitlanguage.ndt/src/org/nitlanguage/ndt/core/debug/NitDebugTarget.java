package org.nitlanguage.ndt.core.debug;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.nitlanguage.ndt.core.debug.events.NitStackFrameEvent;
import org.nitlanguage.ndt.core.debug.events.NitSuspendedEvent;

public class NitDebugTarget implements IDebugTarget, Observer {

	private IProcess process;
	private ILaunch launch;
	// TODO
	// private IThread thread = new NitThread(this);
	private NitDebuggerConnector target;
	private NitDebuggerReader reader;
	private NitDebuggerWriter writer;

	private NitMessageInterpreter messageInterpret;

	private NitThread[] threads;

	private IStackFrame[] stackTrace;

	private boolean isStopped;

	public NitDebugTarget(ILaunch launch, IProcess process, String host,
			int port) throws UnknownHostException, IOException {
		this.target = new NitDebuggerConnector().connect(host, port);
		this.reader = new NitDebuggerReader(this.target);
		this.messageInterpret = new NitMessageInterpreter();
		this.messageInterpret.observe(this.reader);
		this.messageInterpret.addObserver(this);
		this.writer = new NitDebuggerWriter(this.target);
		this.threads = new NitThread[1];
		this.threads[0] = new NitThread(this);
		this.isStopped = true;
		this.stackTrace = new IStackFrame[0];
		this.reader.run();
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
		return true;
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
			e.printStackTrace();
		}
	}

	@Override
	public boolean canResume() {
		return true;
	}

	@Override
	public boolean canSuspend() {
		return true;
	}

	@Override
	public boolean isSuspended() {
		return this.isStopped;
	}

	@Override
	public void resume() throws DebugException {
		try {
			this.writer.continue_exec();
			this.isStopped = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void suspend() throws DebugException {
		try {
			this.writer.suspend_exec();
			this.isStopped = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canDisconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disconnect() throws DebugException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcess getProcess() {
		return this.process;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return true;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof NitSuspendedEvent) {
			this.isStopped = true;
			try {
				this.writer.printStack();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (arg instanceof NitStackFrameEvent) {
			
		}
	}
}
