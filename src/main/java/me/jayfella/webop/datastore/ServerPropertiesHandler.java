/*
 * Copyright (c) 2020 - @FabioZumbi12
 * Last Modified: 14/06/2020 01:10.
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

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ServerPropertiesHandler {
    private final File propertiesFile = new File("server.properties");

    public String getValue(String prefix) {
        String[] lines = readPropertiesFile();
        for (String line : lines) {
            if (line.startsWith(prefix))
                return line;
        }
        return "";
    }

    public void setValue(String prefix, String value) {
        String[] lines = readPropertiesFile();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith(prefix)) {
                lines[i] = prefix + "=" + value;
            }
            result.append(lines[i]).append("\n");
        }

        try {
            Files.write(result, propertiesFile, Charset.defaultCharset());
        } catch (IOException ignored) {
        }
    }

    private String[] readPropertiesFile() {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ignored) {
        }
        return lines.toArray(new String[0]);
    }
}
