/*
 * Copyright (c) 2019 Patrick Scheibe, Dmitry Kashin, Athiele.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package pl.dominikw.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

internal class WindchillWindowPanel : Disposable {

    var content: JPanel? = null
    private var restartWindchillButton: JButton? = null
    private var windchillStatusLabel: JLabel? = null

    init {
        restartWindchillButton!!.addActionListener { restartWindchill() }
        windchillStatusLabel!!.text = "ON"
        windchillStatusLabel!!.icon = PluginIcons.OK

        val tab = listOf(PluginIcons.OK, PluginIcons.KO, PluginIcons.LOAD)
        GlobalScope.launch {
            while (true) {
                windchillStatusLabel!!.icon = when (Random().nextInt(3)) {
                    0 -> tab[0]
                    1 -> tab[1]
                    else -> tab[2]
                }
                delay(1000)
            }
        }
    }

    private fun restartWindchill() {
        Messages.showMessageDialog("Hello world!", "Greeting", Messages.getInformationIcon())
    }

    override fun dispose() {

    }

}
