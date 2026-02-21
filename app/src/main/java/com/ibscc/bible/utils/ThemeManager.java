package com.ibscc.bible.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import com.ibscc.bible.R;

/**
 * Gerenciador de Temas da Bíblia IBSCC
 * Permite alternar entre tema Claro, Escuro e Sépia
 */
public class ThemeManager {
    
    // Constantes
    private static final String PREFS_NAME = "ibscc_bible_preferences";
    private static final String KEY_THEME = "app_theme";
    
    // Tipos de tema
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SEPIA = 2;
    
    // Variáveis
    private final SharedPreferences preferences;
    private final Context context;
    
    /**
     * Construtor
     * @param context Contexto da aplicação
     */
    public ThemeManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Aplica o tema salvo nas preferências
     */
    public void applyTheme() {
        int theme = getSavedTheme();
        applyTheme(theme);
    }
    
    /**
     * Aplica um tema específico
     * @param theme Código do tema (THEME_LIGHT, THEME_DARK ou THEME_SEPIA)
     */
    public void applyTheme(int theme) {
        switch (theme) {
            case THEME_LIGHT:
                context.setTheme(R.style.Theme_IBSSBIVLE);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
                
            case THEME_DARK:
                context.setTheme(R.style.Theme_IBSSBIVLE_Dark);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
                
            case THEME_SEPIA:
                context.setTheme(R.style.Theme_IBSSBIVLE_Sepia);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
                
            default:
                context.setTheme(R.style.Theme_IBSSBIVLE);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
        
        saveTheme(theme);
    }
    
    /**
     * Salva o tema escolhido nas preferências
     * @param theme Código do tema
     */
    private void saveTheme(int theme) {
        preferences.edit()
                .putInt(KEY_THEME, theme)
                .apply();
    }
    
    /**
     * Retorna o tema atualmente salvo
     * @return Código do tema salvo (padrão: THEME_LIGHT)
     */
    public int getSavedTheme() {
        return preferences.getInt(KEY_THEME, THEME_LIGHT);
    }
    
    /**
     * Alterna para o próximo tema (Claro → Escuro → Sépia → Claro...)
     * @return Código do novo tema aplicado
     */
    public int toggleTheme() {
        int currentTheme = getSavedTheme();
        int nextTheme = (currentTheme + 1) % 3;
        applyTheme(nextTheme);
        return nextTheme;
    }
    
    /**
     * Retorna o nome do tema atual
     * @return String com o nome do tema
     */
    public String getThemeName() {
        int theme = getSavedTheme();
        switch (theme) {
            case THEME_LIGHT:
                return "Claro";
            case THEME_DARK:
                return "Escuro";
            case THEME_SEPIA:
                return "Sépia";
            default:
                return "Claro";
        }
    }
    
    /**
     * Verifica se o tema escuro está ativo
     * @return true se estiver em modo escuro
     */
    public boolean isDarkMode() {
        return getSavedTheme() == THEME_DARK;
    }
    
    /**
     * Verifica se o tema sépia está ativo
     * @return true se estiver em modo sépia
     */
    public boolean isSepiaMode() {
        return getSavedTheme() == THEME_SEPIA;
    }
}