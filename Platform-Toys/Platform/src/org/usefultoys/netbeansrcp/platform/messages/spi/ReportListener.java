/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.messages.spi;

import org.usefultoys.netbeansrcp.platform.messages.api.Report;

/**
 *
 * @author x7ws
 */
public interface ReportListener {
    void start(Report report);
    void progress(Report report);
    void ok(Report report);
    void reject(Report report);
    void fail(Report report);
}
