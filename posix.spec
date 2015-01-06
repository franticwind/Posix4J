%define name posix
%define version 1.2.2
%define release 1
%define javahome %(echo ${JAVA_HOME:-/usr/java/jdk1.5.0_16})
%define javaver %(%{javahome}/bin/java -version 2>&1|awk '/java version/{print $3}')
%define javalib %{javahome}/jre/lib/i386
%define javaext /usr/share/java

Summary: Java wrapper for selected Posix APIs
Name: %{name}
Version: %{version}
Release: %{release}
License: LGPL
Group: System Environment/Libraries
Source: %{name}-%{version}.tar.gz
BuildRoot: /var/tmp/%{name}-buildroot
Vendor: Stuart D. Gathman <stuart@bmsi.com>
Packager: Stuart D. Gathman <stuart@bmsi.com>
Requires: j2re
BuildRequires: j2sdk
#BuildRequires: junit.jar

%description
This package provides partial access to the posix API from Java.  It uses a
JNI library which should be portable to other posix systems.  I started this
package with the intent of making it reusable by others.  However, it
only has the classes I have needed for my own projects at present.
I am making the source and docs public so that others can reuse what I
have so far and so that I can collect any additions added by others.  

%prep
%setup -q
echo "Building for j2re-%{javaver}"

%build
CXXFLAGS="-O -pthread -I%{javahome}/include -I%{javahome}/include/linux \
	-DHAVE_MSGBUF -DHAS_SEM_POST -Dsigthreadmask=pthread_sigmask"
LIB="-pthread -L%{javalib} -ljava"
make all CC="$CC" CXXFLAGS="$CXXFLAGS" LIB="$LIB"
mkdir classes
javac -target 1.5 -classpath /bms/java -d $PWD/classes *.java  

%clean
[ -d "$RPM_BUILD_ROOT" -a "$RPM_BUILD_ROOT" != "/" ] && rm -rf $RPM_BUILD_ROOT

%install
[ -d "$RPM_BUILD_ROOT" -a "$RPM_BUILD_ROOT" != "/" ] && rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/bms/lib/java
mkdir -p $RPM_BUILD_ROOT%{javaext}
cd classes
zip -r $RPM_BUILD_ROOT%{javaext}/posix.jar posix
cd -
cp posix.so $RPM_BUILD_ROOT/bms/lib/java/libposix.so

%files
%doc CREDITS TODO COPYING
%defattr(-,root,bin)
%{javaext}/*
/bms/lib/java/*

%post
ln -sf /bms/lib/java/libposix.so ${JAVA_HOME:-%{javahome}}/jre/lib/i386
ln -sf /usr/share/java/posix.jar ${JAVA_HOME:-%{javahome}}/jre/lib/ext

%changelog
* Mon Sep 28 2009 Stuart Gathman <stuart@bmsi.com>	1.2.2-1
- Fix chmod return code.
* Sat Sep 12 2009 Stuart Gathman <stuart@bmsi.com>	1.2.1-1
- Stat.utime(), File.setLastAccessed(), File.setTimes()
* Sat Sep 12 2009 Stuart Gathman <stuart@bmsi.com>	1.2-1
- Break JDK 1.1 support
- support IPC.setPerm()
* Tue May 26 2009 Stuart Gathman <stuart@bmsi.com>	1.1.9-1
- support lstat() and S_IS(XXX,mode)
* Sat Jan 17 2009 Stuart Gathman <stuart@bmsi.com>	1.1.8-2
- symlink .jar and .so to JVM dirs
* Sat Jan 17 2009 Stuart Gathman <stuart@bmsi.com>	1.1.8-1
- Build with jdk-1.5.0_16
- Add IPC.euid, IPC.egid, Stat.umask()
- Support IPC_PRIVATE and IPC_CREAT with SharedMem
* Wed May 18 2005 Stuart Gathman <stuart@bmsi.com>	1.1.7-2
- put classes in posix.jar
- Build for jdk1.5.0_11
* Wed May 18 2005 Stuart Gathman <stuart@bmsi.com>	1.1.7-1
- fix Errno with old GNU strerror_r()
- export IPC.getId()
* Wed May 18 2005 Stuart Gathman <stuart@bmsi.com>	1.1.6-1
- fix Passwd parsing of empty field
- more robust Signal
- port to Centos-4
* Wed May 18 2005 Stuart Gathman <stuart@bmsi.com>	1.1.5-2
- export Signal.kill
- implement SemSet
