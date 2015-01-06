PKG=posix-1.2.2
CVSTAG=posix-1_2_2

# preprocessor flags:
# POSIX_STRERROR_R=0	old glibc strerror_r
# POSIX_STRERROR_R=1	posix strerror_r
# HAVE_MSGBUF		struct msgbuf declared by system as per posix
# HAVE_UTIMES		system provides utimes() as well as utime()
# HAS_SEM_POST

# AIX 4.1.5 w/ GNUC and AIX linker
#CC=gcc
#CXXFLAGS = -O -I/usr/jdk_base/include -I/usr/jdk_base/include/aix -mthreads
#LIB=-mthreads -Wl,-blibpath:/lib:/usr/lib -lpthreads \
#	-L/usr/jdk_base/lib/aix/native_threads -ljava
#JAVALIB = /usr/jdk_base/lib/aix/native_threads/libposix.a

# AIX 5.X with native IBM compilers and linkers
#CXX=xlC_r
#CXXFLAGS = -O -I$(JAVA_HOME)/include -DHAVE_MSGBUF -DHAVE_UTIMES -DHAS_SEM_POST -DPOSIX_STRERROR_R=1
#LIB= -qmkshrobj -blibpath:/lib:/usr/lib -lpthreads -L$(JAVA_HOME)/jre/bin
#JAVALIB = $(JAVA_HOME)/lib/aix/native_threads/libposix.a

# FreeBSD 7/8 i386/amd64
#CXXFLAGS = -O -pthread -I/usr/local/jdk1.6.0/include \
#	-I/usr/local/jdk1.6.0/include/freebsd \
#	-fPIC -DPAGE_SIZE=4096 -DPOSIX_STRERROR_R \
#	-Dsigthreadmask=pthread_sigmask
#LIB =  -pthread -L/usr/local/jdk1.6.0/jre/lib/amd64/ -ljava
#JAVALIB = libposix.so

# RHEL 4
CXXFLAGS = -fPIC -Wall -O -pthread -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -DHAVE_UTIMES \
        -DHAS_SEM_POST -DHAVE_MSGBUF -Dsigthreadmask=pthread_sigmask
LIB =   -pthread -L$(JAVA_HOME)/jre/lib/amd64/ -ljava -lc
JAVALIB = $(JAVA_HOME)/jre/lib/amd64/libposix.so

# Solaris 2.6
#CCFLAGS = -O -I/usr/java/include -I/usr/java/include/solaris -DSIGMAX=MAXSIG
#LIB = -L/usr/java/lib/sparc/native_threads -ljava -lpthread
#JAVALIB = /usr/java/lib/sparc/native_threads/libposix.so
#CCC = gcc

all:	posix.so

POSIX_OBJS = ipc.o Errno.o CPtr.o Signal.o Stat.o

ipc.o:	ipc.cc posix_MsgQ.h posix_IPC.h posix_SharedMem.h

Errno.o:	posix_Errno.h Errno.cc

CPtr.o:	CPtr.cc posix_CPtr.h posix_Malloc.h

Signal.o: Signal.cc posix_Signal.h

File.o:	File.cc posix_File.h

posix.so: $(POSIX_OBJS)
	g++ -shared -Wall -fPIC -o posix.so $(POSIX_OBJS) $(LIB)

#posix.exp:	ipc.o Errno.o CPtr.o Signal.o
#	genexp ipc.o Errno.o CPtr.o Signal.o >posix.exp
#posix.so:	posix.exp
#	ld -s -o posix.so ipc.o Errno.o CPtr.o Signal.o	\
#	  -bnoentry -bM:SRE -bE:posix.exp -blibpath:/lib:/usr/lib \
#	  -lpthreads -lc_r -L/java/lib/aix/native_threads -ljava

install:	posix.so
	su root -c "cp posix.so $(JAVALIB)"

javac:
	$(JAVA_HOME)/bin/javac -d bin *.java

sender:
	$(JAVA_HOME)/bin/java -cp bin test.Sender

receiver:
	$(JAVA_HOME)/bin/java -cp bin test.Receiver

shmem:
	$(JAVA_HOME)/bin/java -cp bin test.SharedMemTest

tar:
	ln -s . $(PKG) || true
	tar -cvhf $(PKG).tar $(PKG)/*.java $(PKG)/*.h \
		$(PKG)/*.cc $(PKG)/Makefile $(PKG)/*.html \
		$(PKG)/unfinished/*.java $(PKG)/*.spec \
		$(PKG)/TODO $(PKG)/CREDITS $(PKG)/COPYING
	gzip $(PKG).tar; rm $(PKG)

SRCTAR = $(PKG).tar.gz
$(SRCTAR):
	cvs export -r$(CVSTAG) -d $(PKG) java/posix
	tar cvf $(PKG).tar $(PKG)
	gzip -f $(PKG).tar
	rm -r $(PKG)

cvstar: $(SRCTAR)

zip:
	zip posix *.java *.h *.cc *.html Makefile

doc:	*.java package.html
	polardoc -author -version -package -notree -noindex \
		-d $(DOCDIR) *.java
