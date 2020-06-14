/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 00:14.
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package me.jayfella.webop.datastore;

import me.jayfella.webop.PluginContext;
import me.jayfella.webop.core.MessagePriority;
import me.jayfella.webop.core.WebOpMessage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class MessageHandler {
    private final PluginContext context;
    private final File messageFolder;
    private final List<WebOpMessage> messages;
    private int biggestMessageId;

    public MessageHandler(final PluginContext context) {
        this.biggestMessageId = 0;
        this.context = context;
        this.messageFolder = new File(this.context.getPlugin().getDataFolder() + File.separator + "/messages");
        if (!this.messageFolder.isDirectory()) {
            this.messageFolder.mkdirs();
        }
        this.messages = this.loadMessages();
    }

    private List<WebOpMessage> loadMessages() {
        final List<WebOpMessage> loadedMessages = new ArrayList<WebOpMessage>();
        for (final String file : this.messageFolder.list()) {
            Label_0441:
            {
                if (file.endsWith(".txt")) {
                    final File msgFile = new File(this.messageFolder + File.separator + file);
                    final List<String> lines = new ArrayList<String>();
                    try (final BufferedReader br = new BufferedReader(new FileReader(msgFile.getAbsoluteFile()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            lines.add(line);
                        }
                    } catch (IOException ex) {
                        this.context.getPlugin().getLogger().log(Level.WARNING, "Unable to open message {0}", file);
                        this.context.getPlugin().getLogger().log(Level.WARNING, ex.getMessage());
                        break Label_0441;
                    }
                    final int msgId = Integer.parseInt(file.replace(".txt", ""));
                    if (msgId > this.biggestMessageId) {
                        this.biggestMessageId = msgId;
                    }
                    final String user = lines.get(0);
                    final String timeStamp = lines.get(1);
                    final MessagePriority priority = MessagePriority.valueOf(lines.get(2));
                    final StringBuilder message = new StringBuilder();
                    for (int i = 3; i < lines.size(); ++i) {
                        message.append(lines.get(i));
                    }
                    final String parsedMessage = message.toString().replace("\n", " ");
                    final WebOpMessage newMessage = new WebOpMessage(msgId, user, timeStamp, priority, parsedMessage);
                    loadedMessages.add(newMessage);
                }
            }
        }
        return loadedMessages;
    }

    public void deleteMessage(final int messageId) {
        final Iterator<WebOpMessage> iterator = this.messages.iterator();
        while (iterator.hasNext()) {
            final WebOpMessage msg = iterator.next();
            if (msg.getId() == messageId) {
                new File(this.messageFolder + File.separator + msg.getId() + ".txt").delete();
                iterator.remove();
                return;
            }
        }
    }

    public WebOpMessage createMessage(final String user, final MessagePriority priority, final String message) {
        ++this.biggestMessageId;
        final int newId = this.biggestMessageId;
        final String currentStamp = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new Date());
        final File msgFile = new File(this.messageFolder + File.separator + newId + ".txt");
        try (final BufferedWriter bw = new BufferedWriter(new FileWriter(msgFile, true))) {
            bw.write(user);
            bw.newLine();
            bw.write(currentStamp);
            bw.newLine();
            bw.write(priority.toString());
            bw.newLine();
            bw.write(message);
            bw.close();
        } catch (IOException ignored) {
        }
        final WebOpMessage newMessage = new WebOpMessage(newId, user, currentStamp, priority, message);
        this.messages.add(newMessage);
        return newMessage;
    }

    public WebOpMessage getMessage(final int id) {
        for (WebOpMessage message : this.messages) {
            if (message.getId() == id) {
                return message;
            }
        }
        return null;
    }

    public List<WebOpMessage> getMessages() {
        return this.messages;
    }

    public String createWebSocketString(final WebOpMessage message) {
        return "id=" + message.getId() + ";" + "user=" + message.getUser() + ";" + "time=" + message.getTimeStamp() + ";" + "priority=" + message.getPriority().name() + ";" + "message=" + message.getMessage();
    }
}
