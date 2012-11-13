package ru.grails

class PrintStackTraceWrapper {

    private static class PrintWriterWrapper extends PrintWriter {
        private printWriter
        private filterPackages

        PrintWriterWrapper(PrintWriter printWriter, String[] filterPackages) {
            super(System.err)
            this.printWriter = printWriter
            this.filterPackages = filterPackages
        }

        void println(Object o) {
            String message = String.valueOf(o);
            synchronized (PrintStackTraceWrapper) {
                if (PrintStackTraceWrapper.doPrint(message, filterPackages)) {
                    printWriter.println(message);
                }
            }
        }
    }

    private static class PrintStreamWrapper extends PrintStream {
        private printStream
        private filterPackages

        PrintStreamWrapper(PrintStream printStream, String[] filterPackages) {
            super(System.err)
            this.printStream = printStream
            this.filterPackages = filterPackages
        }

        void println(Object o) {
            String message = String.valueOf(o);
            synchronized (PrintStackTraceWrapper) {
                if (PrintStackTraceWrapper.doPrint(message, filterPackages)) {
                    printStream.println(message);
                }
            }
        }
    }

    static inject(String[] filterPackages) {

        Throwable.metaClass.printStackTrace = {
            delegate.printStackTrace(System.err)
        }

        Throwable.metaClass.printStackTrace = { PrintStream stream ->
            def method = Throwable.getMethod("printStackTrace", PrintStream)
            def wrapper = new PrintStreamWrapper(stream, filterPackages)
            method.invoke(delegate, wrapper)
        }

        Throwable.metaClass.printStackTrace = { PrintWriter writer ->
            def method = Throwable.getMethod("printStackTrace", PrintWriter)
            def wrapper = new PrintWriterWrapper(writer, filterPackages)
            method.invoke(delegate, wrapper)
        }

    }

    static boolean doPrint(String message, String[] filterPackages) {
        return filterPackages.any { p -> message.contains(p) }
    }

}
