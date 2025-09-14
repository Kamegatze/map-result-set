package com.kamegatze.map.result.set.logger;

import java.util.Objects;

import com.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

public final class LoggerImpl implements Logger {

    private final Level level;
    private final Logger log;

    public LoggerImpl(Logger log) {
        this.level = Level.valueOf(System.getProperty(GeneralConstantUtility.LOGGING_LEVEL_KEY, Level.WARN.name()).toUpperCase());
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public String getName() {
        return log.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled() && level.equals(Level.TRACE);
    }

    @Override
    public void trace(String msg) {
        if (level.equals(Level.TRACE)) {
            log.trace(msg);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (level.equals(Level.TRACE)) {
            log.trace(format, arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (level.equals(Level.TRACE)) {
            log.trace(format, arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (level.equals(Level.TRACE)) {
            log.trace(format, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (level.equals(Level.TRACE)) {
            log.trace(msg, t);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker) && level.equals(Level.TRACE);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (level.equals(Level.TRACE)) {
            log.trace(marker, msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (level.equals(Level.TRACE)) {
            log.trace(marker, format, arg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (level.equals(Level.TRACE)) {
            log.trace(marker, format, arg1, arg2);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (level.equals(Level.TRACE)) {
            log.trace(marker, format, argArray);
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (level.equals(Level.TRACE)) {
            log.trace(marker, msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled() && level.equals(Level.DEBUG);
    }

    @Override
    public void debug(String msg) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(format, arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker) && level.equals(Level.DEBUG);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(marker, msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(marker, format, arg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(marker, format, arg1, arg2);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(marker, format, arguments);
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (level.equals(Level.TRACE) || level.equals(Level.DEBUG)) {
            log.debug(marker, msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled() && level.equals(Level.INFO);
    }

    @Override
    public void info(String msg) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(msg);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(format, arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(format, arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(format, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker) && level.equals(Level.INFO);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(marker, msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(marker, format, arg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(marker, format, arg1, arg2);
        }
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(marker, format, arguments);
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (!level.equals(Level.WARN) && !level.equals(Level.ERROR)) {
            log.info(marker, msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled() && level.equals(Level.WARN);
    }

    @Override
    public void warn(String msg) {
        if (!level.equals(Level.ERROR)) {
            log.warn(msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (!level.equals(Level.ERROR)) {
            log.warn(format, arg);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (!level.equals(Level.ERROR)) {
            log.warn(format, arguments);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (!level.equals(Level.ERROR)) {
            log.warn(format, arg1, arg2);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (!level.equals(Level.ERROR)) {
            log.warn(msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker) && level.equals(Level.WARN);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (!level.equals(Level.ERROR)) {
            log.warn(marker, msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (!level.equals(Level.ERROR)) {
            log.warn(marker, format, arg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (!level.equals(Level.ERROR)) {
            log.warn(marker, format, arg1, arg2);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (!level.equals(Level.ERROR)) {
            log.warn(marker, format, arguments);
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (!level.equals(Level.ERROR)) {
            log.warn(marker, msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled() && level.equals(Level.ERROR);
    }

    @Override
    public void error(String msg) {
        log.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        log.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        log.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker) && level.equals(Level.ERROR);
    }

    @Override
    public void error(Marker marker, String msg) {
        log.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        log.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        log.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        log.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        log.error(marker, msg, t);
    }
}
