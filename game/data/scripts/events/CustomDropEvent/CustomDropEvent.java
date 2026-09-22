/*
 * Copyright (c) 2025 L2Journey Project
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package events.CustomDropEvent;

import com.l2journey.gameserver.model.quest.LongTimeEvent;

/**
 * Evento de drop customizado.<br>
 * Dropa itens configurados em config.xml de acordo com o level do monstro e/ou monstros específicos definidos na droplist.
 */
public class CustomDropEvent extends LongTimeEvent
{
	private CustomDropEvent()
	{
		// Toda a lógica de leitura do config.xml, registro de drops e
		// agendamento de início/fim é tratada pelo construtor de LongTimeEvent.
	}
	
	public static void main(String[] args)
	{
		new CustomDropEvent();
	}
}
