/** Posix for Java.

<h3> Why Posix for Java? </h3>

A frequently asked question on Java newsgroups is "How do I use unix
signals (or messages or environment variables or ...) in Java?"  The answer
is, you can't with standard Java.  Equivalent features are in Java. For
example, Properties instead of environment variables or Sockets instead
of the various forms of unix IPC.  
<p>
However, sometimes it is necessary to interface with code written for
the unix environment - and that code can't be rewritten to use Sockets.
In those cases, it would be nice to have simple access to the Posix API
without everyone having to reinvent the wheel.
<p>
One solution is the <code>posix</code> package.  This package provides
access to the posix API from Java.  It uses a JNI library which should
be portable to other posix systems.  I started this package with the
intent of making it reusable by others.  However, it only has the classes
I have needed for my own projects at present :-).  I am making the source
and docs public so that others can reuse what I have so far and so that
I can collect any additions added by others.  
<p>
There is another posix API for Java used by the 
<a href="http://www.jython.org/">Jython project</a>:
<a href="http://sourceforge.net/projects/jnios">jnios</a>.
I will be looking at incorporating features from that package, or
getting rid of my own entirely.  I will not get rid of 'posix' if the
jnios package requires jython.

<h3> SysV IPC </h3>

There is fairly complete support for IPC.  The {@link posix.MsgQ} class wraps
message queues, and {@link posix.SemSet} wraps semaphores.
<p>
The {@link posix.SharedMem} class wraps an ipc shared memory segment.
Attaching a SharedMem returns a {@link posix.CPtr} which allows safe 
and portable access to C format structures in the shared memory.  (You
can only trash stuff in the share memory, not anywhere else.)  Similarly,
{@link posix.Malloc} safely allocates and accesses blocks of C memory
which can be passed to C apis and are not garbage collected.

<h3> The Passwd class </h3>

The {@link posix.Passwd} class provides read only access to files in the unix
<code>/etc/passwd</code> format.  It is a pure Java implementation.

<h3> The Stat function </h3>

The {@link posix.Stat} class provides the most common fields from
<code>stat.h</code> and a <code>stat()</code> method to fill them in.  The 
{@link posix.File} class extends java.io.File to provide
additional attributes such as lastAccessed().

<h3> The Posix Signal API for Java </h3>

The {@link posix.Signal} class models posix signals,
converting them to Java Event notifications.  Common posix signals
are provided as preinitialized static Signal objects.  Because the JVM
uses signals internally, I do not export the API for trapping any
signal.  The ones I provide I believe are safe.

<h4>Signal Event implementation</h4>

A single Java Thread listens for signals by executing <code>sigwait()</code>
through JNI to wait on a semaphore.  The JNI code actually spawns another
non-Java thread to call sigwait, because that thread must be cancelled
in order to change the set of signals handled by Java.
The Java Signal Thread generates a {@link posix.SignalEvent} for each signal.
<p>
This implementation should be portable to any pthreads platform.
It uses mutexes and condition variables - which the JVM would also have
to use.  Currently, there is a small window during which a signal
will take the default action when calling Signal.setAction().  Set up
your SignalListeners at startup, and there should be no problem.

<h3> Further Information </h3>

The JNI source is in C++.  The C++ is used as a "better C".  In particular,
there are no static initializers, RTTI, or exceptions used since there is no
standard JVM support for C++.  
(Only these C++ features require special runtime support.)
We currently use gcc-3.4.6 on Centos4 with Sun JDK 1.5.0_16, and  gcc-2.7.2 on
AIX with the IBM JDK 1.1.6.4.
*/
package posix;
